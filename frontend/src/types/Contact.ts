import {Skill} from "./Skill.ts";
import {JobOfferHeader} from "./JobOffer.ts";

export type ContactType = {
    id: number;
    first_name: string;
    last_name: string;
    category: Category;
    ssn: string;
    emails: Email[];
    addresses: Address[];
    phone_numbers: Telephone[];
}

export type ProfessionalAdditionalType = {
    skills: Skill[];
    location: string;
    daily_rate: number;
    notes: string;
    employment_state: EmploymentState;
    job_offers: JobOfferHeader[];
}

export type CustomerAdditionalType = {
    notes: string;
    preferences: string;
    job_offers: JobOfferHeader[];
}

export type ProfessionalType = ContactType & { related_professional: ProfessionalAdditionalType };
export type CustomerType = ContactType & { related_customer: CustomerAdditionalType };

export type Email = {
    id: number;
    email: string;
}

export type Address = {
    id: number;
    street: string;
    number: string;
    city: string;
    postal_code: string;
    country: string;
}

export type Telephone = {
    id: number;
    telephone: string;
}

export type ContactHeader = {
    id: number,
    first_name: string,
    last_name: string,
    category: Category,
}

export enum Category {
    UNKNOWN = 'Unknown',
    CUSTOMER = 'Customer',
    PROFESSIONAL = 'Professional',
}

export function parseCategory(category: string): Category {
    switch (category.toLowerCase()) {
        case 'customer':
            return Category.CUSTOMER;
        case 'professional':
            return Category.PROFESSIONAL;
        default:
            return Category.UNKNOWN;
    }
}

export enum EmploymentState {
    EMPLOYED = 'Employed',
    UNEMPLOYED = 'Unemployed',
    NOT_AVAILABLE = 'Not_available',
}

export function parseEmploymentState(employmentState: string): EmploymentState {
    switch (employmentState.toLowerCase()) {
        case 'employed':
            return EmploymentState.EMPLOYED;
        case 'unemployed':
            return EmploymentState.UNEMPLOYED;
        default:
            return EmploymentState.NOT_AVAILABLE;
    }
}

export function cleanToContact(values: ContactType & ProfessionalAdditionalType & CustomerAdditionalType): ContactType {
    return {
        id: values.id,
        first_name: values.first_name,
        last_name: values.last_name,
        category: values.category,
        ssn: values.ssn,
        emails: values.emails,
        addresses: values.addresses,
        phone_numbers: values.phone_numbers,
    } as ContactType
}

export function cleanToProfessional(values: ContactType & ProfessionalAdditionalType & CustomerAdditionalType): ProfessionalType {
    return {
        id: values.id,
        first_name: values.first_name,
        last_name: values.last_name,
        category: values.category,
        ssn: values.ssn,
        emails: values.emails,
        addresses: values.addresses,
        phone_numbers: values.phone_numbers,
        related_professional: {
            notes: values.notes,
            skills: values.skills,
            location: values.location,
            daily_rate: values.daily_rate,
            employment_state: values.employment_state,
            job_offers: values.job_offers || [],
        }
    }
}

export function cleanToCustomer(values: ContactType & ProfessionalAdditionalType & CustomerAdditionalType): CustomerType {
    return {
        id: values.id,
        first_name: values.first_name,
        last_name: values.last_name,
        category: values.category,
        ssn: values.ssn,
        emails: values.emails,
        addresses: values.addresses,
        phone_numbers: values.phone_numbers,
        related_customer: {
            notes: values.notes,
            preferences: values.preferences,
            job_offers: values.job_offers || [],
        }
    }
}