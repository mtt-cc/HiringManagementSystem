import {AuthInformationContextInterface} from "./types/AuthInformation.ts";
import {createContext} from "react";
import {Navigation} from "./types/Navigation.ts";

export const AuthContext = createContext<AuthInformationContextInterface>({
    user: undefined
})

export const NavigationContext = createContext<Navigation>({
    navigate: undefined,
    routes: []
})