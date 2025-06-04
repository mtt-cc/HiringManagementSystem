import {NavigateFunction} from "react-router-dom";
import React from "react";

export type Navigation = {
    navigate: NavigateFunction | undefined;
    routes: Record<string, NavigationRoute>
}

export type NavigationRoute = {
    to: (...args: any[]) => void;
    path: string;
    title: string;
    pageElement: React.ReactNode;
}