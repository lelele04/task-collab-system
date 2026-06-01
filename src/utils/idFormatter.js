export const formatUserId = (id) => {
  if (!id) return '-'
  return `U${String(id).padStart(3, '0')}`
}

export const formatTeamId = (id) => {
  if (!id) return '-'
  return `T${String(id).padStart(3, '0')}`
}

export const formatTaskId = (id) => {
  if (!id) return '-'
  return `K${String(id).padStart(3, '0')}`
}

export const formatApplicationId = (id) => {
  if (!id) return '-'
  return `A${String(id).padStart(3, '0')}`
}