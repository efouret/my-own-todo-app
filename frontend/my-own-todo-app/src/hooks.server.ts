import type {Handle} from '@sveltejs/kit';

export const handle: Handle = async ({event, resolve}) => {
    // Implement any server-side logic or middleware here
    return resolve(event);
};
