import { API_USERS } from 'constant';
import IFetch from 'api/fetch';
import { IUser } from 'types/User';

export default {
  getUser: (authToken?: string | undefined) => IFetch<IUser>({ method: 'GET', url: API_USERS, authToken: authToken }),
};
