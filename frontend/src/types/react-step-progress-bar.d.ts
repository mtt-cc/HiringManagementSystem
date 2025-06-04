declare module 'src/types/react-step-progress-bar' {
    import * as React from 'react';

    export interface ProgressBarProps {
        percent?: number;
        filledBackground?: string;
        height?: string;
        width?: string;
        unfilledBackground?: string;
        hasStepZero?: boolean;
        children?: React.ReactNode;
    }

    export const ProgressBar: React.FC<ProgressBarProps>;

    export interface StepProps {
        transition?: string;
        children?: ({
                        accomplished: boolean,
                        transitionState: string,
                        index: number,
                        position: number,
                    }) => React.Node;
        position?: number;
        index?: number;
        accomplished?: boolean;
    }

    export const Step: React.FC<StepProps>;
}