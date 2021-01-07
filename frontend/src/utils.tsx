import cookie from 'cookie';
import { AUTH_TOKEN } from 'constant';

export const getAuthToken = (cookies: string | undefined): string | null => {
  const allCookies = cookie.parse(cookies || '');
  return allCookies[AUTH_TOKEN] || null;
};
