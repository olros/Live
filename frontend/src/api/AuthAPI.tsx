import { API_AUTH, API_AUTH_SIGNIN, API_AUTH_SIGNUP } from 'constant';
import IFetch from 'api/fetch';
import { ICreateUser } from 'types/User';
import { SuccessResponse, LoginResponse, LoginRequest } from 'types/Request';

export default {
  login: (loginData: LoginRequest) => IFetch<LoginResponse>({ method: 'POST', url: `${API_AUTH}/${API_AUTH_SIGNIN}`, data: loginData }),
  signUp: (newUser: ICreateUser) => IFetch<SuccessResponse>({ method: 'POST', url: `${API_AUTH}/${API_AUTH_SIGNUP}`, data: newUser }),
};
