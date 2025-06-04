import {Form, InputGroup} from "react-bootstrap";
import React, {ReactNode} from "react";
import ErrorFeedback from "./ErrorFeedback.tsx";

export type SelectOption = {
    value: string,
    label: string
}

interface BasicSelectInputProps {
    name: string,
    label: string,
    value: string | number | string[] | undefined,
    touched: boolean | undefined,
    error: string | undefined,
    leadingIcon?: ReactNode,
    trailingIcon?: ReactNode,
    onChange: (v: React.FocusEvent<HTMLSelectElement>) => void,
    onBlur: (e: React.FocusEvent<HTMLSelectElement>) => void,
    options: SelectOption[],
    placeholderOption?: string,
    disabled?: boolean,
}

function BasicSelectInput(props: BasicSelectInputProps) {

    return (
        <Form.Group controlId={props.name}>
            <label className="ms-2 mb-1 mt-2" style={{fontSize: "1.1rem"}}>{props.label}</label>
            <InputGroup>
                {
                    props.leadingIcon ?
                        <InputGroup.Text style={{height: "3.6rem"}}>{props.leadingIcon}</InputGroup.Text>
                        : null
                }
                <Form.Select
                    aria-label={props.label}
                    name={props.name}
                    value={props.value}
                    onChange={props.onChange}
                    onBlur={props.onBlur}
                    isValid={props.touched && !props.error}
                    isInvalid={props.touched && !!props.error}
                    disabled={props.disabled}
                    style={{height: '3.5rem', borderBottomRightRadius: props.trailingIcon ? '' : '0.25rem', borderTopRightRadius: props.trailingIcon ? '' : '0.25rem'}}
                >
                    {
                        props.placeholderOption ?
                            <option value="">{props.placeholderOption}</option>
                            : null
                    }
                    {
                        props.options.map((option, index) => {
                            return (
                                <option
                                    key={index}
                                    value={option.value}
                                >
                                    {option.label}
                                </option>
                            );
                        })
                    }
                </Form.Select>
                <ErrorFeedback error={props.error}/>
                {
                    props.trailingIcon ?
                        <InputGroup.Text style={{height: "3.6rem"}}>{props.trailingIcon}</InputGroup.Text>
                        : null
                }
            </InputGroup>
        </Form.Group>
    );
}

export default BasicSelectInput;