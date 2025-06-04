import {Configuration} from "./configuration_crm.ts";
import {AuthInformation} from "../types/AuthInformation.ts";
import {Method} from "./utils.ts";
import {ErrorRFC7807} from "../components/ErrorModalRFC7807.tsx";

export function fetchMe(
    ok: (user: AuthInformation) => void,
    err: (error: ErrorRFC7807) => void,
    fin: () => void = () => {
    }
) {
    async function fetchMeAsync() {
        try {
            const response = await fetch(Configuration.routes.me, {
                method: Method.GET.toString(),
                credentials: "include",
            });
            if (response.ok) {
                return await response.json();
            } else {
                throw await response.json() as ErrorRFC7807;
            }
        } catch (e : Error | ErrorRFC7807 | any) {
            if (e instanceof Error) {
                throw {
                    instance: "frontend",
                    title: "Network error",
                    detail: `Unable to connect to the server (${e})`
                } as ErrorRFC7807;
            }

            throw e;
        }
    }

    fetchMeAsync().then(ok).catch(err).finally(fin);
}