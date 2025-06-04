export type AuthInformation = {
    loginUrl : string;
    logoutUrl : string;

    status : boolean;
    name : string | null;
    fullname : string | null;
    email: string | null;
    email_verified: boolean | null;
    roles : string[];
    // principal : object | null;
    xsrfToken : string | null;
}

export interface AuthInformationContextInterface {
    user: AuthInformation | undefined;
}