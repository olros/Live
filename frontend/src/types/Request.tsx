export interface SuccessResponse {
  detail: string;
}

export interface ErrorResponse {
  error: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
}
