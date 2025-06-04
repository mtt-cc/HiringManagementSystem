import React, {ReactNode, useEffect, useState} from 'react';
import {Dropdown, Form, InputGroup} from "react-bootstrap";
import ErrorFeedback from "./ErrorFeedback.tsx";
import {TEXT_MAX_LENGTH} from "../../utils.tsx";

interface SuggestedTextInputProps {
    name: string,
    label: string,
    value: string,
    touched: boolean | undefined,
    error: string | undefined,
    placeholder?: string,
    leadingIcon?: ReactNode,
    trailingIcon?: ReactNode,
    type?: string,
    onChange: (e: React.ChangeEvent<HTMLInputElement>) => void,
    setFieldValue: (value: string) => void,
    onBlur: (e: React.FocusEvent<HTMLInputElement>) => void,
    disabled?: boolean,
    readonly?: boolean,
    suggestions: string[]
}

function SuggestedTextInput(props: SuggestedTextInputProps) {
    const [visible, setVisible] = useState(false);
    const [suggestions, setSuggestions] = useState<string[]>(props.suggestions);

    useEffect(() => {
        setSuggestions(props.suggestions.filter((s) => s.toLowerCase().includes(props.value.toLowerCase())))
    }, [props.value]);

    return (
        <Form.Group controlId={props.name}>
            <InputGroup>
                {
                    props.leadingIcon ?
                        <InputGroup.Text style={{height: "3.6rem"}}>{props.leadingIcon}</InputGroup.Text>
                        : null
                }
                <Form.FloatingLabel controlId={props.name} label={props.label}>
                    <Form.Control
                        type={props.type}
                        name={props.name}
                        placeholder={props.placeholder ? props.placeholder : ""}
                        value={props.value}
                        onChange={props.onChange}
                        onBlur={(e: React.FocusEvent<HTMLInputElement>) => {
                            setTimeout(() => setVisible(false), 200);
                            props.onBlur(e);
                        }}
                        onFocus={() => setVisible(true)}
                        isValid={props.touched && !props.error}
                        isInvalid={props.touched && !!props.error}
                        disabled={props.disabled}
                        readOnly={props.readonly}
                        autoComplete="off"
                        max={TEXT_MAX_LENGTH}
                    />
                    <Dropdown.Menu
                        show={visible}
                        style={{width: "100%"}}
                    >
                        {
                            suggestions.length > 0 ?
                                suggestions.map((suggestion, index) => {
                                    return (
                                        <Dropdown.Item
                                            key={index}
                                            onClick={() => {
                                                props.setFieldValue(suggestion)
                                                setVisible(false);
                                            }}
                                        >
                                            {suggestion}
                                        </Dropdown.Item>
                                    );
                                })
                                :
                                <Dropdown.Item disabled>
                                    No suggestions
                                </Dropdown.Item>
                        }
                    </Dropdown.Menu>
                    <ErrorFeedback error={props.error}/>
                </Form.FloatingLabel>
                {
                    props.trailingIcon ?
                        <InputGroup.Text style={{height: "3.6rem"}}>{props.trailingIcon}</InputGroup.Text>
                        : null
                }
            </InputGroup>
        </Form.Group>
    );
}

export default SuggestedTextInput;