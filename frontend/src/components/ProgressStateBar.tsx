import "react-step-progress-bar/styles.css";
import {ProgressBar, Step} from "react-step-progress-bar";      // ignore this error
import {min} from "../utils.tsx";

export type ProgressStateBarEntryType = {
    index: number;
    name: string;
    date: string | undefined;
    time: string | undefined;
    color: string;
    half: boolean;
}

interface ProgressStateBarProps {
    states: ProgressStateBarEntryType[];
    currentState: number;
    aborted: boolean;
}

function ProgressStateBarEntry(props: { state: ProgressStateBarEntryType, accomplished: boolean, color: string }) {
    return (
        <div
            style={{
                width: "1.4rem",
                height: "1.4rem",
                borderRadius: "50%",
                backgroundColor: props.accomplished ? props.color : "#E0E0E0",
                border: `0.2rem solid ${props.accomplished ? props.color : "#E0E0E0"}`,
            }}
            className={`indexedStep ${props.accomplished ? "accomplished" : null}`}
        >
            <p
                className="p-0 m-0"
                style={{
                    position: "relative",
                    fontSize: "0.8rem",
                    top: "-1.5rem",
                    left: "-2.5rem",
                    fontWeight: props.accomplished ? "bold" : "normal",
                    whiteSpace: "nowrap",
                    height: "1rem",
                }}
            >
                {props.accomplished && props.state.date ? props.state.date : ""}
                {props.accomplished && props.state.time ? ` - ${props.state.time}` : ""}
            </p>
            <p
                className="p-0 m-0"
                style={{
                    position: "relative",
                    top: "0.75rem",
                    whiteSpace: "nowrap",
                    fontWeight: props.accomplished ? "bold" : "normal",
                }}
            >{props.state.name}</p>
        </div>
    )
}

function ProgressStateBar(props: ProgressStateBarProps) {
    if (props.aborted) {
       return (
           <ProgressBar
               percent={100}
               filledBackground={"red"}
               unfilledBackground={"#E0E0E0"}
           >
               {props.states.map((state, index) => (
                   <Step key={index}>
                       {() => (
                           <ProgressStateBarEntry
                               accomplished={true}
                               state={state}
                               color={"red"}
                           />
                       )}
                   </Step>
               ))}
           </ProgressBar>
       )
    }
    else {
        const color = props.states[min(props.currentState, props.states.length-1)].color;
        const half = props.states[min(props.currentState, props.states.length-1)].half;
        return (
            <ProgressBar
                percent={evaluatePercent(props.currentState, props.states.length, half)}
                filledBackground={color}
                unfilledBackground={"#E0E0E0"}
            >
                {props.states.map((state, index) => (
                    <Step key={index}>
                        {({accomplished}) => (
                            <ProgressStateBarEntry
                                accomplished={accomplished}
                                state={state}
                                color={color}
                            />
                        )}
                    </Step>
                ))}
            </ProgressBar>
        );
    }
}

function evaluatePercent(currentState: number, statesLength: number, half: boolean) {
    const stepSize = 100 / (statesLength - 1);
    var percent = currentState * stepSize;
    if (half) {
        percent += stepSize / 2;
    }
    return min(percent, 100);
}

export default ProgressStateBar;