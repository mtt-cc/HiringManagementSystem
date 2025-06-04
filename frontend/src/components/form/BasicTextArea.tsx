import React, {useEffect, useRef} from 'react';
import {Form} from "react-bootstrap";
import ErrorFeedback from "./ErrorFeedback.tsx";
import {FREE_TEXT_MAX_LENGTH} from "../../utils.tsx";

interface BasicTextAreaProps {
    name: string,
    label: string,
    value: string | number | string[] | undefined,
    touched: boolean | undefined,
    error: string | undefined,
    placeholder?: string,
    onChange: (e: React.ChangeEvent<HTMLInputElement>) => void,
    onBlur: (e: React.FocusEvent<HTMLInputElement>) => void,
    disabled?: boolean,
    readonly?: boolean,
}

function BasicTextArea(props: BasicTextAreaProps) {
    const ref = useRef<HTMLTextAreaElement | null>(null);
    const resize = () => {
        if (ref.current) {
            ref.current.style.height = 'auto';
            ref.current.style.height = `${ref.current.scrollHeight}px`;
        }
    }

    useEffect(() => {
        resize();
        window.addEventListener('resize', resize);
    }, []);

    return (
        <Form.Group controlId={props.name}>
            <Form.FloatingLabel controlId={props.name} label={props.label}>
                <Form.Control
                    ref={ref}
                    as="textarea"
                    name={props.name}
                    placeholder={props.placeholder ? props.placeholder : ""}
                    value={props.value}
                    onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                        props.onChange(e);
                        resize();
                    }}
                    onBlur={props.onBlur}
                    isValid={props.touched && !props.error}
                    isInvalid={props.touched && !!props.error}
                    disabled={props.disabled}
                    readOnly={props.readonly}
                    style={{resize: "none", overflow: "hidden"}}
                    max={FREE_TEXT_MAX_LENGTH}
                />
                <ErrorFeedback error={ props.error } />
            </Form.FloatingLabel>
        </Form.Group>
    );
}

export default BasicTextArea;