import ErrorModalRFC7807, {ErrorRFC7807} from "../components/ErrorModalRFC7807.tsx";
import axios from "axios";

/**
 * Fetches data from the server
 * @param url : string - the URL to fetch data from
 * @param xsrfToken : string | null - the XSRF token to include in the request
 * @param method : Method - the method to use for the request
 * @param body : object | undefined - the body to send with the request
 * @param expected_body : boolean - if set to true, the function will try to parse the response body as JSON, otherwise it will return null
 * @param as_json : boolean - if set to true, the function will parse the response body as JSON, otherwise it will return the raw text
 *
 * @returns Promise<object> - the response body parsed as JSON if expected_body is true, otherwise null
 *
 * @throws object - the error contained in the response body
 */
export async function fetchDataAsync(
    url : string,
    xsrfToken: string | null = null,
    method: Method = Method.GET,
    expected_body: ExpectedBody = ExpectedBody.JSON,
    body: object | string | undefined = undefined,
) {
    const response = await fetch(url, {
        method: method.toString(),
        credentials: "include",
        headers: {
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": xsrfToken || ""
        },
        body: body ? JSON.stringify(body) : null
    });
    if (response.ok) {
        try {
            switch (expected_body) {
                case ExpectedBody.JSON:
                    return await response.json();
                case ExpectedBody.TEXT:
                    return await response.text();
                case ExpectedBody.NONE:
                    return null;
            }
        } catch (e) {
            throw {
                title: "Unable to parse response",
                detail: "An error occurred while processing the response",
                status: response.status,
                type: "about:blank"
            } as ErrorRFC7807;
        }
    } else {
        let error : ErrorRFC7807;
        try {
            error = await response.json();
        } catch (e) {
            throw {
                title: "Unable to parse error",
                detail: "An error occurred while processing the error",
                status: response.status,
                type: "about:blank"
            } as ErrorRFC7807;
        }
        throw error;
    }
}

export enum Method {
    GET = "GET",
    POST = "POST",
    PUT = "PUT",
    DELETE = "DELETE"
}

export enum ExpectedBody {
    NONE,
    JSON,
    TEXT
}