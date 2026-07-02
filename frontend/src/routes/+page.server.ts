import { PUBLIC_BACKEND_URL } from '$env/static/public';
import type { PageServerLoad } from "./$types";

export const load: PageServerLoad = async ({ cookies, fetch }) => {
  const token = cookies.get('session');
  const res = await fetch(`${PUBLIC_BACKEND_URL}/api/commodities`, {
    headers: token ? { cookie: `JSESSIONID=${token}` } : {},
  });
  return { commodities: res.ok ? await res.json() : [] };
};
