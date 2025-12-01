export type ageGroup = typeof ageGroups;

export const ageGroups = {
  TEENS: "10대",
  TWENTIES: "20대",
  THIRTIES: "30대",
  FORTIES: "40대",
  FIFTIES: "50대",
  SIXTIES: "60대",
  SEVENTIES_PLUS: "70대 이상",
}

export function getAgeGroupLabel(ageGroupKey: keyof typeof ageGroups): string {
  return ageGroups[ageGroupKey];
}