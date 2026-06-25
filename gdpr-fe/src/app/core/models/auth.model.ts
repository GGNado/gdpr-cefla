export interface LoginResponse {
  token: string;
  type: string;
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
  expiresIn: number;
}
