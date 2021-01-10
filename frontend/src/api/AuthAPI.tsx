import { API_AUTH } from 'constant';
import IFetch from 'api/fetch';
import { ICreateUser } from 'types/User';
import { SuccessResponse, LoginResponse, LoginRequest } from 'types/Request';

export default {
  login: (loginData: LoginRequest) => IFetch<LoginResponse>({ method: 'POST', url: `${API_AUTH}/signin`, data: loginData }),
  signUp: (newUser: ICreateUser) => IFetch<SuccessResponse>({ method: 'POST', url: `${API_AUTH}/signup`, data: newUser }),
};
