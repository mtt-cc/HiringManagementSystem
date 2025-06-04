import React from 'react';
import {Form as FormikForm} from "formik";
import {Button, Col, Container, Row} from "react-bootstrap";
import BasicTextInput from "./form/BasicTextInput.tsx";
import {IoIosPerson} from "react-icons/io";
import BasicSelectInput from "./form/BasicSelectInput.tsx";
import {
    Address,
    Category,
    ContactType, CustomerAdditionalType,
    Email, EmploymentState,
    ProfessionalAdditionalType,
    Telephone
} from "../types/Contact.ts";
import {capitalize, TelephoneRegex} from "../utils.tsx";
import {BiCategory} from "react-icons/bi";
import {IoCardOutline} from "react-icons/io5";
import {Formik, FormikErrors} from "formik";
import {AiOutlineMail, AiOutlineNumber} from "react-icons/ai";
import {BsDashLg, BsMailboxFlag, BsTelephone} from "react-icons/bs";
import {GiPositionMarker, GiWorld} from "react-icons/gi";
import {PiCityLight} from "react-icons/pi";
import Spacer from "./Spacer.tsx";
import * as Yup from "yup";
import BasicTextArea from "./form/BasicTextArea.tsx";
import {TbPigMoney} from "react-icons/tb";
import {GrWorkshop} from "react-icons/gr";
import EditableBadgeList from "./EditableBadgeList.tsx";
import {BadgeEntry} from "./BadgeList.tsx";
import {Skill} from "../types/Skill.ts";

function ContactForm(props: {
    initialValues: ContactType & ProfessionalAdditionalType & CustomerAdditionalType,
    handleSubmit: (values: ContactType & ProfessionalAdditionalType & CustomerAdditionalType, {setSubmitting}: any) => void,
    submitLabel: string,
    editMode?: boolean
}) {
    const validationSchema = Yup.object({
        first_name: Yup.string().required("First name is required"),
        last_name: Yup.string().required("Last name is required"),
        category: Yup.mixed<Category>().oneOf(Object.values(Category)),
        ssn: Yup.string().required("SSN is required"),
        emails: Yup.array().of(
            Yup.object().shape({
                id: Yup.number(),
                email: Yup.string().email("Invalid email").required("Email is required"),
            })
        ),
        addresses: Yup.array().of(
            Yup.object().shape({
                id: Yup.number(),
                street: Yup.string().required("Street is required"),
                number: Yup.string().required("Number is required"),
                city: Yup.string().required("City is required"),
                postal_code: Yup.string().required("Postal code is required"),
                country: Yup.string().required("Country is required"),
            })
        ),
        phone_numbers: Yup.array().of(
            Yup.object().shape({
                id: Yup.number(),
                telephone: Yup.string().required("Phone number is required").matches(TelephoneRegex, "Invalid phone number format"),
            })
        ),
        preferences: Yup.string(),
        notes: Yup.string(),
        daily_rate: Yup.number().when("category", {
            is: (category: Category) => category === Category.PROFESSIONAL,
            then: (s) => s.required("Daily rate is required").min(0, "Daily rate cannot be negative"),
            otherwise: (s) => s.notRequired()
        }),
        location: Yup.string().when("category", {
            is: (category: Category) => category === Category.PROFESSIONAL,
            then: (s) => s.required("Location is required"),
            otherwise: (s) => s.notRequired()
        }),
        employment_state: Yup.mixed<EmploymentState>().when("category", {
            is: (category: Category) => category === Category.PROFESSIONAL,
            then: (s) => s.required("Employment state is required"),
            otherwise: (s) => s.notRequired()
        }),
        skills: Yup.array().of(Yup.string().required("Skill is required")),
    });

    return (
        <Formik
            initialValues={props.initialValues}
            validationSchema={validationSchema}
            onSubmit={props.handleSubmit}
        >
            {({
                  isSubmitting,
                  values,
                  touched,
                  errors,
                  handleChange,
                  handleBlur,
                  setFieldValue,
                  setFieldTouched
              }) => (
                <FormikForm className="p-5">
                    <Row>
                        <Col md={6}>
                            <BasicTextInput
                                name="first_name"
                                label="Fist Name"
                                type="text"
                                placeholder="Enter first name"
                                value={values.first_name}
                                touched={touched.first_name}
                                error={errors.first_name}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                forceMinHeight
                                leadingIcon={<IoIosPerson size="1.3rem"/>}
                                disabled={isSubmitting}
                            />
                        </Col>
                        <Col md={6}>
                            <BasicTextInput
                                name="last_name"
                                label="Last Name"
                                type="text"
                                placeholder="Enter last name"
                                value={values.last_name}
                                touched={touched.last_name}
                                error={errors.last_name}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                forceMinHeight
                                leadingIcon={<IoIosPerson size="1.3rem"/>}
                                disabled={isSubmitting}
                            />
                        </Col>
                    </Row>

                    {/* Category */}
                    <Row>
                        <BasicSelectInput
                            name="category"
                            label="Category"
                            value={values.category}
                            touched={touched.category}
                            error={errors.category}
                            onChange={(e: React.ChangeEvent<HTMLSelectElement>) => {
                                setFieldValue("category", e.target.value as Category);
                                switch (e.target.value) {
                                    case Category.CUSTOMER:
                                        setFieldValue("skills", []);
                                        setFieldValue("location", "");
                                        setFieldValue("daily_rate", 0);
                                        setFieldValue("notes", "");
                                        setFieldValue("employment_state", "");
                                        break;
                                    case Category.PROFESSIONAL:
                                        setFieldValue("preferences", "");
                                        setFieldValue("notes", "");
                                        break;
                                    default:
                                        setFieldValue("preferences", "");
                                        setFieldValue("notes", "");
                                        setFieldValue("skills", []);
                                        setFieldValue("location", "");
                                        setFieldValue("daily_rate", 0);
                                        setFieldValue("notes", "");
                                        setFieldValue("employment_state", "");
                                }
                            }}
                            onBlur={handleBlur}
                            options={Object.values(Category).map((category) => ({
                                value: category,
                                label: capitalize(category)
                            }))}
                            leadingIcon={<BiCategory size="1.3rem"/>}
                            disabled={(props.editMode && props.initialValues.category !== Category.UNKNOWN) || isSubmitting}
                        />
                    </Row>

                    {/* SSN */}
                    <Row className="mt-5">
                        <BasicTextInput
                            name="ssn"
                            label="SSN"
                            type="text"
                            placeholder="Enter SSN"
                            value={values.ssn}
                            touched={touched.ssn}
                            error={errors.ssn}
                            onChange={handleChange}
                            onBlur={handleBlur}
                            forceMinHeight
                            leadingIcon={<IoCardOutline size="1.3rem"/>}
                            disabled={isSubmitting}
                        />
                    </Row>

                    {/* Emails */}
                    <Row className="mt-4">
                        {
                            values.emails.map((email, index) => (
                                <BasicTextInput
                                    key={index}
                                    className={"mb-3"}
                                    name={`emails[${index}]`}
                                    label={`Email ${index + 1}`}
                                    value={email.email}
                                    touched={(touched.emails ? touched.emails[index] : undefined)}
                                    error={
                                        (errors.emails ? errors.emails[index] as FormikErrors<Email> | undefined : undefined)?.email
                                    }
                                    onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                                        setFieldValue(`emails[${index}]`, {
                                            id: email.id,
                                            email: e.target.value
                                        });
                                    }}
                                    onBlur={handleBlur}
                                    type={"email"}
                                    leadingIcon={<AiOutlineMail size={"1.3rem"}/>}
                                    trailingIcon={
                                        <BsDashLg size={"1.3rem"}/>
                                    }
                                    trailingIconOnClick={
                                        () => {
                                            if (isSubmitting) return;
                                            setFieldValue(`emails`, [...(values.emails.slice(0, index)), ...(values.emails.slice(index + 1))])
                                            setFieldTouched(`emails[${index}]`, false);
                                        }
                                    }
                                    disabled={isSubmitting}
                                />
                            ))
                        }
                        <div className="pe-3 d-flex justify-content-end">
                            <Button className="w-25" style={{height: "3rem"}} disabled={isSubmitting}
                                    onClick={() => {
                                        setFieldValue("emails", [...values.emails, {email: "", id: (values.emails.length + 3)*(-1)}])
                                    }}>
                                Add Email
                            </Button>
                        </div>
                    </Row>

                    {/* Phone Numbers */}
                    <Row className="mt-4">
                        {
                            values.phone_numbers.map((pn, index) => (
                                <BasicTextInput
                                    key={index}
                                    className={"mb-3"}
                                    name={`phone_numbers[${index}]`}
                                    label={`Phone number ${index + 1}`}
                                    value={pn.telephone}
                                    touched={(touched.phone_numbers ? touched.phone_numbers[index] : undefined)}
                                    error={
                                        (errors.phone_numbers ? errors.phone_numbers[index] as FormikErrors<Telephone> | undefined : undefined)?.telephone
                                    }
                                    onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                                        setFieldValue(`phone_numbers[${index}]`, {
                                            id: pn.id,
                                            telephone: e.target.value
                                        });
                                    }}
                                    onBlur={handleBlur}
                                    type={"text"}
                                    leadingIcon={<BsTelephone size={"1.3rem"}/>}
                                    trailingIcon={
                                        <BsDashLg size={"1.3rem"}/>
                                    }
                                    trailingIconOnClick={
                                        () => {
                                            if (isSubmitting) return;
                                            setFieldValue(`phone_numbers`, [...(values.phone_numbers.slice(0, index)), ...(values.phone_numbers.slice(index + 1))]);
                                            setFieldTouched(`phone_numbers[${index}]`, false);
                                        }
                                    }
                                    disabled={isSubmitting}
                                />
                            ))
                        }
                        <div className="pe-3 d-flex justify-content-end">
                            <Button className="w-25" style={{height: "3rem"}} disabled={isSubmitting}
                                    onClick={() => {
                                        setFieldValue("phone_numbers", [...values.phone_numbers, {telephone: "", id: (values.phone_numbers.length + 3)*(-1)}])
                                    }}>
                                Add Phone Number
                            </Button>
                        </div>
                    </Row>

                    <Row className="mt-4">
                        {
                            values.addresses.map((addr, index) => (
                                <Container fluid key={index}>
                                    <Row>
                                        <label
                                            className="ms-2 mb-1 mt-2"
                                            style={{fontSize: "1.1rem"}}
                                        >
                                            Address {index + 1}
                                        </label>
                                    </Row>
                                    <Row>
                                        <Col className="col-8">
                                            <BasicTextInput
                                                className={"mb-3"}
                                                name={`addresses[${index}].street`}
                                                label={`Street`}
                                                value={addr.street}
                                                touched={(touched.addresses ? touched.addresses[index]?.street : undefined)}
                                                error={
                                                    (errors.addresses ? errors.addresses[index] as FormikErrors<Address> | undefined : undefined)?.street
                                                }
                                                onChange={handleChange}
                                                onBlur={handleBlur}
                                                type={"text"}
                                                leadingIcon={<GiPositionMarker size={"1.3rem"}/>}
                                                disabled={isSubmitting}
                                            />
                                        </Col>
                                        <Col className="col-3">
                                            <BasicTextInput
                                                className={"mb-3"}
                                                name={`addresses[${index}].number`}
                                                label={`Number`}
                                                value={addr.number}
                                                touched={(touched.addresses ? touched.addresses[index]?.number : undefined)}
                                                error={
                                                    (errors.addresses ? errors.addresses[index] as FormikErrors<Address> | undefined : undefined)?.number
                                                }
                                                onChange={handleChange}
                                                onBlur={handleBlur}
                                                type={"text"}
                                                leadingIcon={<AiOutlineNumber size={"1.3rem"}/>}
                                                disabled={isSubmitting}
                                            />
                                        </Col>
                                        <Col>
                                            <Button
                                                className="w-100"
                                                style={{height: "3.6rem", borderColor: "lightgray"}}
                                                variant="outline-danger"
                                                onClick={() => {
                                                    setFieldValue("addresses", [...(values.addresses.slice(0, index)), ...(values.addresses.slice(index + 1))])
                                                    setFieldTouched(`addresses[${index}]`, undefined);
                                                }}
                                                disabled={isSubmitting}
                                            >
                                                <BsDashLg size={"1.3rem"}/>
                                            </Button>
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Col className="col-4">
                                            <BasicTextInput
                                                key={index}
                                                className={"mb-3"}
                                                name={`addresses[${index}].city`}
                                                label={`City`}
                                                value={addr.city}
                                                touched={(touched.addresses ? touched.addresses[index]?.city : undefined)}
                                                error={
                                                    (errors.addresses ? errors.addresses[index] as FormikErrors<Address> | undefined : undefined)?.city
                                                }
                                                onChange={handleChange}
                                                onBlur={handleBlur}
                                                type={"text"}
                                                leadingIcon={<PiCityLight size={"1.3rem"}/>}
                                                disabled={isSubmitting}
                                            />
                                        </Col>
                                        <Col className="col-4">
                                            <BasicTextInput
                                                key={index}
                                                className={"mb-3"}
                                                name={`addresses[${index}].postal_code`}
                                                label={`Postal Code`}
                                                value={addr.postal_code}
                                                touched={(touched.addresses ? touched.addresses[index]?.postal_code : undefined)}
                                                error={
                                                    (errors.addresses ? errors.addresses[index] as FormikErrors<Address> | undefined : undefined)?.postal_code
                                                }
                                                onChange={handleChange}
                                                onBlur={handleBlur}
                                                type={"text"}
                                                leadingIcon={<BsMailboxFlag size={"1.3rem"}/>}
                                                disabled={isSubmitting}
                                            />
                                        </Col>
                                        <Col>
                                            <BasicTextInput
                                                key={index}
                                                className={"mb-3"}
                                                name={`addresses[${index}].country`}
                                                label={`Country`}
                                                value={addr.country}
                                                touched={(touched.addresses ? touched.addresses[index]?.country : undefined)}
                                                error={
                                                    (errors.addresses ? errors.addresses[index] as FormikErrors<Address> | undefined : undefined)?.country
                                                }
                                                onChange={handleChange}
                                                onBlur={handleBlur}
                                                type={"text"}
                                                leadingIcon={<GiWorld size={"1.3rem"}/>}
                                                disabled={isSubmitting}
                                            />
                                        </Col>
                                    </Row>
                                    {
                                        index < values.addresses.length - 1 ?
                                            <>
                                                <Spacer height="1rem" borderBottom="solid .1rem lightgray"/>
                                                <Spacer height={".5rem"}/>
                                            </>
                                            : null
                                    }
                                </Container>
                            ))
                        }
                        <div className="pe-3 d-flex justify-content-end">
                            <Button className="w-25" style={{height: "3rem"}} disabled={isSubmitting}
                                    onClick={() => {
                                        setFieldValue("addresses", [...values.addresses, {
                                            id: (values.addresses.length + 3)*(-1),
                                            street: "",
                                            number: "",
                                            city: "",
                                            postal_code: "",
                                            country: ""
                                        }])
                                    }}>
                                Add Address
                            </Button>
                        </div>
                    </Row>

                    {/* Customer specific fields*/}
                    {
                        values.category === Category.CUSTOMER && <Row className="mt-5">
                            <BasicTextArea
                                name="preferences"
                                label="Customer Preferences"
                                value={values.preferences}
                                touched={touched.preferences}
                                error={errors.preferences}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                disabled={isSubmitting}
                            />
                        </Row>
                    }

                    {
                        values.category === Category.CUSTOMER && <Row className="mt-5">
                            <BasicTextArea
                                name="notes"
                                label="Customer Notes"
                                value={values.notes}
                                touched={touched.notes}
                                error={errors.notes}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                disabled={isSubmitting}
                            />
                        </Row>
                    }

                    {/* Professional specific fields*/}

                    {
                        values.category === Category.PROFESSIONAL && <Row className="mt-5">
                            <BasicTextInput
                                name="daily_rate"
                                label="Daily Rate"
                                type="number"
                                placeholder="Enter daily rate"
                                value={values.daily_rate}
                                touched={touched.daily_rate}
                                error={errors.daily_rate}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                forceMinHeight
                                leadingIcon={<TbPigMoney size="1.3rem"/>}
                                disabled={isSubmitting}
                            />
                        </Row>
                    }

                    {
                        values.category === Category.PROFESSIONAL && <Row className="mt-5">
                            <BasicTextInput
                                name="location"
                                label="Work location"
                                type="text"
                                placeholder="Enter location"
                                value={values.location}
                                touched={touched.location}
                                error={errors.location}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                forceMinHeight
                                leadingIcon={<GiPositionMarker size="1.3rem"/>}
                                disabled={isSubmitting}
                            />
                        </Row>
                    }

                    {
                        values.category === Category.PROFESSIONAL && <Row className="mt-5">
                            <Col className="col-3 ps-5">
                                <p className="mb-0" style={{marginTop: ".35rem"}}>Professional's skills</p>
                            </Col>
                            <Col>
                                <EditableBadgeList
                                    badges={values.skills.map((s : Skill) => ({key: s, text: s} as BadgeEntry))}
                                    label="Skills"
                                    onRemove={async (entry) => {
                                        setFieldValue("skills", values.skills.filter((s : Skill) => s !== entry.text))
                                    }}
                                    onAdd={async (entry) => {
                                        setFieldValue("skills", [...values.skills, entry.text as Skill])
                                    }}
                                    left
                                    disabled={isSubmitting}
                                />
                            </Col>
                        </Row>
                    }

                    {
                        values.category === Category.PROFESSIONAL && <Row className="mt-4">
                            <BasicSelectInput
                                name="employment_state"
                                label="Employment State"
                                value={values.employment_state}
                                touched={touched.employment_state}
                                error={errors.employment_state}
                                onChange={(e: React.ChangeEvent<HTMLSelectElement>) => {
                                    setFieldValue("employment_state", e.target.value as EmploymentState);
                                }}
                                onBlur={handleBlur}
                                options={Object.values(EmploymentState).map((state) => ({
                                    value: state,
                                    label: capitalize(state)
                                }))}
                                leadingIcon={<GrWorkshop size="1.3rem"/>}
                                disabled={isSubmitting}
                            />
                        </Row>
                    }

                    {
                        values.category === Category.PROFESSIONAL && <Row className="mt-5">
                            <BasicTextArea
                                name="notes"
                                label="Professional Notes"
                                value={values.notes}
                                touched={touched.notes}
                                error={errors.notes}
                                onChange={handleChange}
                                onBlur={handleBlur}
                                disabled={isSubmitting}
                            />
                        </Row>
                    }


                    {/* Submit Button */}
                    <Button variant="primary" type="submit" disabled={isSubmitting} className="w-100 mt-5"
                            style={{height: "3rem"}}>
                        {props.submitLabel}
                    </Button>
                </FormikForm>
            )}
        </Formik>
    );
}

export default ContactForm;