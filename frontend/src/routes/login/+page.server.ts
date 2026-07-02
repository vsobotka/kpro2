import { fail, redirect } from '@sveltejs/kit';
import { PUBLIC_BACKEND_URL } from '$env/static/public';
import type { Actions } from './$types';

export const actions: Actions = {
  default: async ({ request, fetch, cookies }) => {
    const data = await request.formData();

    const res = await fetch(`${PUBLIC_BACKEND_URL}/login`, {
      method: 'POST',
      headers: { 'content-type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({
        username: String(data.get('username') ?? ''),
        password: String(data.get('password') ?? ''),
      }),
      redirect: 'manual',
    });
    if (!res.ok) return fail(401, { error: 'Invalid username or password' });

    const setCookie = res.headers.get('set-cookie');
    const jsessionid = setCookie?.match(/JSESSIONID=([^;]+)/)?.[1];
    if (!jsessionid) return fail(401, { error: 'Backend did not return a session' });

    cookies.set('session', jsessionid, {
      httpOnly: true,
      sameSite: 'lax',
      path: '/',
      secure: false,
    });
    throw redirect(303, '/');
  },
};