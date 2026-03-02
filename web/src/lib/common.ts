export interface Pagination {
  currentPage: number;
  totalPages: number;
  totalElements: number;
  isLast: boolean;
}

export const buildQuery = (params: Record<string, any>) => {
  const searchParams = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null) {
      if (Array.isArray(value)) {
        value.forEach(item => searchParams.append(key, String(item)));
      } else {
        searchParams.append(key, String(value));
      }
    }
  });
  return searchParams.toString();
};