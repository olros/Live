import cookie from 'cookie';
import Cookies from 'js-cookie';
import { AUTH_TOKEN } from 'constant';

export const getAuthTokenServer = (cookies: string | undefined): string | undefined => {
  const allCookies = cookie.parse(cookies || '');
  return allCookies[AUTH_TOKEN] || undefined;
};

export const getAuthTokenLocal = (): string | undefined => Cookies.get(AUTH_TOKEN);
