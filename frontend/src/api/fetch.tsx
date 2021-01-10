import Cookies from 'js-cookie';
import { API_URL, AUTH_TOKEN } from 'constant';
import { ErrorResponse } from 'types/Request';

type RequestMethodType = 'GET' | 'POST' | 'PUT' | 'DELETE';

type FetchProps = {
  method: RequestMethodType;
  url: string;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  data?: Record<string, unknown | any>;
  withAuth?: boolean;
  authToken?: string | unknown;
};

// eslint-disable-next-line comma-spacing
const IFetch = <T,>({ method, url, data, withAuth = true, authToken }: FetchProps): Promise<T> => {
  const urlAddress = `${API_URL}/${url}`;
  const headers = new Headers();
  headers.append('Content-Type', 'application/json');

  if (withAuth) {
    const token = authToken || Cookies.get(AUTH_TOKEN);
    !token || headers.append('Authorization', `Bearer ${token}`);
  }

  return fetch(request(method, urlAddress, headers, data || {}))
    .then((response) => {
      const contentType = response.headers.get('content-type');
      if (!contentType || !contentType.includes('application/json') || !response.ok || response.json === undefined) {
        if (response.json === undefined) {
          throw new Error(response.statusText);
        } else {
          return response.json().then((responseData: ErrorResponse) => {
            throw new Error(responseData.error);
          });
        }
      }

      return response.json().then((responseData: T) => responseData);
    })
    .catch((error) => {
      throw error;
    });
};

export default IFetch;

const request = (method: RequestMethodType, url: string, headers: Headers, data: Record<string, unknown>) => {
  return new Request(method === 'GET' ? url + argsToParams(data) : url, {
    method: method,
    headers: headers,
    ...(method !== 'GET' && { body: JSON.stringify(data) }),
  });
};

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const argsToParams = (data: Record<string, any>) => {
  let args = '?';
  for (const key in data) {
    if (Array.isArray(data[key])) {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      for (const value in data[key] as any) {
        args += `&${key}=${data[key][value]}`;
      }
    } else {
      args += `&${key}=${data[key]}`;
    }
  }
  return args;
};
