package com.teamtask.aspect;

import com.teamtask.entity.User;
import com.teamtask.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect
@Component
public class AuthAspect {

    @Autowired
    private UserService userService;

    @Around("execution(* com.teamtask.controller..*(..))")
    public Object authCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getName();
        String controllerName = joinPoint.getTarget().getClass().getSimpleName();
        String requestMethod = request.getMethod();

        if (methodName.equals("login") || methodName.equals("register")) {
            return joinPoint.proceed();
        }

        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("未登录或token无效");
        }

        Long userId = extractUserIdFromToken(token);
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        boolean isAdmin = "ADMIN".equals(user.getRole());

        if (controllerName.equals("TeamApplicationController")) {
            if (methodName.equals("apply") || methodName.equals("applyCreateTeam")) {
                return joinPoint.proceed();
            }
            if (methodName.equals("getById") || methodName.equals("getApplicationsByUserId")) {
                return joinPoint.proceed();
            }
            if (!isAdmin) {
                throw new RuntimeException("权限不足，只有管理员可以审批团队申请");
            }
        }

        if (controllerName.equals("TeamController")) {
            if ("GET".equals(requestMethod)) {
                return joinPoint.proceed();
            }
            if (!isAdmin) {
                throw new RuntimeException("权限不足，只有管理员可以管理团队");
            }
        }

        if (controllerName.equals("TaskController")) {
            if ("GET".equals(requestMethod)) {
                return joinPoint.proceed();
            }
            if (methodName.equals("claimTask")) {
                return joinPoint.proceed();
            }
            if (methodName.equals("updateTaskStatus")) {
                String status = request.getParameter("status");
                if ("PENDING_REVIEW".equals(status)) {
                    return joinPoint.proceed();
                }
            }
            if (!isAdmin) {
                throw new RuntimeException("权限不足，只有管理员可以管理任务");
            }
        }

        if (controllerName.equals("UserController")) {
            if ("GET".equals(requestMethod)) {
                return joinPoint.proceed();
            }
            if (!isAdmin) {
                throw new RuntimeException("权限不足，只有管理员可以管理用户");
            }
        }

        return joinPoint.proceed();
    }

    private Long extractUserIdFromToken(String token) {
        String[] parts = token.split("_");
        if (parts.length >= 2) {
            return Long.parseLong(parts[1]);
        }
        throw new RuntimeException("无效的token");
    }
}