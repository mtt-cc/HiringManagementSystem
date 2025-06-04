import {Form, InputGroup} from "react-bootstrap";
import React, {ReactNode} from "react";
import ErrorFeedback from "./ErrorFeedback.tsx";
import {FormikTouched} from "formik";
import {TEXT_MAX_LENGTH} from "../../utils.tsx";

interface BasicTextInputProps {
    name: string,
    label: string,
    value: string | number | string[] | undefined,
    touched: boolean | FormikTouched<any> | undefined,
    error: string | undefined,
    placeholder?: string,
    leadingIcon?: ReactNode,
    trailingIcon?: ReactNode,
    trailingIconOnClick?: () => void,
    type?: string,
    onChange: (e: React.ChangeEvent<HTMLInputElement>) => void,
    onBlur: (e: React.FocusEvent<HTMLInputElement>) => void,
    disabled?: boolean,
    readonly?: boolean,
    forceMinHeight?: boolean,
    className?: string
}

function BasicTextInput(props: BasicTextInputProps) {
    return (
        <Form.Group className={props.className} controlId={props.name} style={{minHeight: props.forceMinHeight ? "5rem" : ""}}>
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
                        onBlur={props.onBlur}
                        isValid={props.touched && !props.error}
                        isInvalid={props.touched && !!props.error}
                        disabled={props.disabled}
                        readOnly={props.readonly}
                        max={props.type !== "number" ? TEXT_MAX_LENGTH : undefined}
                    />
                    <ErrorFeedback error={props.error}/>
                </Form.FloatingLabel>
                {
                    props.trailingIcon ?
                        <InputGroup.Text onClick={props.trailingIconOnClick} style={{height: "3.6rem", cursor: props.trailingIconOnClick ? "pointer" : ""}}>{props.trailingIcon}</InputGroup.Text>
                        : null
                }
            </InputGroup>
        </Form.Group>
    );
}

export default BasicTextInput;

/*
const initialValues = {
    first_name: '',
    last_name: '',
    category: Category.UNKNOWN,
    ssn: '',
    emails: [] as Email[],
    addresses: [] as Address[],
    phone_numbers: [] as Telephone[],
} as ContactType;

const validationSchema = Yup.object({
    first_name: Yup.string().required("First name is required"),
    last_name: Yup.string().required("Last name is required"),
    category: Yup.mixed<Category>().oneOf(Object.values(Category)),
    ssn: Yup.string().required("SSN is required"),
    emails: Yup.array().of(
        Yup.object().shape({
            email: Yup.string().email("Invalid email").required("Email is required"),
        })
    ),
    addresses: Yup.array().of(
        Yup.object().shape({
            street: Yup.string().required("Street is required"),
            number: Yup.string().required("Number is required"),
            city: Yup.string().required("City is required"),
            postal_code: Yup.string().required("Postal code is required"),
            country: Yup.string().required("Country is required"),
        })
    ),
    phone_numbers: Yup.array().of(
        Yup.object().shape({
            telephone: Yup.string().required("Phone number is required").test('is-valid-phone-number', 'Phone number is not valid', value => {
                if (!value) return false; // Handle required
                const phoneNumber = parsePhoneNumber(value);
                return phoneNumber && isValidNumber(phoneNumber.number);
            }),
        })
    ),
});
 */