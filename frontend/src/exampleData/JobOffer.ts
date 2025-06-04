import {JobOffer, JobOfferHeader, JobOfferStatus} from "../types/JobOffer.ts";
import {CustomerType, ProfessionalType} from "../types/Contact.ts";

export const JobOfferExampleData = {
    id: 1,                                      // ok
    title: "Job Offer Title",                   // ok
    description: "Job Offer Description",       // ok
    duration: 1,                                // ok
    value: 1,                                   // ok
    notes: "Job Offer Notes",
    history: [
        {
            id: 0,
            name: JobOfferStatus.CREATED,
            date: "10/10/2021",
            time: "10:00",
        },
        {
            id: 1,
            name: JobOfferStatus.SELECTION_PHASE,
            date: "11/10/2021",
            time: "9:00",
        },
        {
            id: 2,
            name: JobOfferStatus.CANDIDATE_PROPOSAL,
            date: "19/10/2021",
            time: "17:30",
        },
        {
            id: 3,
            name: JobOfferStatus.CONSOLIDATED,
            date: "20/10/2021",
            time: "10:00",
        },
        {
            id: 4,
            name: JobOfferStatus.DONE,
            date: "21/10/2021",
            time: "10:00",
        },
        {
            id: 5,
            name: JobOfferStatus.SELECTION_PHASE,
            date: "29/10/2021",
            time: "9:00",
        },
        // {
        //     id: 6,
        //     name: JobOfferStatus.ABORTED,
        //     date: "30/10/2021",
        //     time: "9:00",
        // },
    ],                        // ok
    skills: [
        {id: 1, skill: "Skill 1"},
        {id: 2, skill: "Skill 2"},
        {id: 3, skill: "Skill 3"}
    ],                          // ok
    status: JobOfferStatus.SELECTION_PHASE,             // ok
    customer: {
        id: 1,
        category: "Customer",
        first_name: "Customer",
        last_name: "1",
        ssn: "123456789",
        notes: "Customer notes",
        preferences: "Customer preferences",
        emails: [],
        addresses: [],
        phone_numbers: [],
    },
    professional: {
        id: 1,
        category: "Professional",
        first_name: "Professional",
        last_name: "1",
        ssn: "987654321",
        skills: [
            {id: 1, skill: "Skill 1"},
            {id: 2, skill: "Skill 2"},
        ],
        emails: [],
        addresses: [],
        phone_numbers: [],
        daily_rate: 100,
        location: "Location",
        notes: "Professional notes",
        employment_state: "Employed",
    },
    // professional: null,
    candidates: [
        {
            candidate: {
                id: 1,
                category: "Professional",
                first_name: "Professional",
                last_name: "1",
                skills: [
                    {id: 1, skill: "Skill 1"},
                    {id: 2, skill: "Skill 2"},
                ],
                emails: [],
                addresses: [],
                phone_numbers: [],
                daily_rate: 100,
                location: "Location",
                notes: "Professional notes",
                employment_state: "Unemployed",
                ssn: "987654321",
            } as ProfessionalType,
            notes: "Not a good professional",
            verified: true,
        },
        {
            candidate: {
                id: 2,
                category: "Professional",
                first_name: "Professional",
                last_name: "2",
                skills: [
                    {id: 2, skill: "Skill 2"},
                    {id: 3, skill: "Skill 3"},
                ],
                emails: [],
                addresses: [],
                phone_numbers: [],
                daily_rate: 100,
                location: "Location",
                notes: "Professional notes",
                employment_state: "Employed",
                ssn: "987654321",
            } as ProfessionalType,
            notes: "",
            verified: false,
        },
        {
            candidate: {
                id: 3,
                category: "Professional",
                first_name: "Professional",
                last_name: "3",
                skills: [
                    {id: 1, skill: "Skill 1"},
                    {id: 3, skill: "Skill 3"},
                    {id: 4, skill: "Skill 4"},
                ],
                emails: [],
                addresses: [],
                phone_numbers: [],

                daily_rate: 100,
                location: "Location",
                notes: "Professional notes",
                employment_state: "Not available",
                ssn: "987654321",
            } as ProfessionalType,
            notes: "A very good professional",
            verified: true,
        },
        {
            candidate: {
                id: 4,
                category: "Professional",
                first_name: "Professional",
                last_name: "4",
                skills: [
                    {id: 1, skill: "Skill 1"},
                    {id: 2, skill: "Skill 2"},
                    {id: 3, skill: "Skill 3"},
                ],
                emails: [],
                addresses: [],
                phone_numbers: [],
                daily_rate: 100,
                location: "Location",
                notes: "Professional notes",
                employment_state: "Not available",
                ssn: "987654321",
            } as ProfessionalType,
            notes: "",
            verified: false,
        },
        {
            candidate: {
                id: 5,
                category: "Professional",
                first_name: "Professional",
                last_name: "5",
                skills: [
                    {id: 1, skill: "Skill 1"},
                    {id: 2, skill: "Skill 2"},
                    {id: 3, skill: "Skill 3"},
                ],
                emails: [],
                addresses: [],
                phone_numbers: [],
                daily_rate: 100,
                location: "Location",
                notes: "Professional notes",
                employment_state: "Unemployed",
                ssn: "987654321",
            } as ProfessionalType,
            notes: "",
            verified: true,
        }
    ],
} as JobOffer;

export const JobOfferListExampleData = [
    {
        id: 1,
        title: "Test job offer",
        description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies.",
        status: JobOfferStatus.CREATED,
        customer: {
            id: 1,
            first_name: "Test customer",
            last_name: "1",
            category: "Customer",
            ssn: "123456789",
            emails: [],
            addresses: [],
            phone_numbers: [],
            notes: "Customer notes",
            preferences: "Customer preferences"
        } as CustomerType
    },
    {
        id: 2,
        title: "Test job offer",
        description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies.",
        status: JobOfferStatus.SELECTION_PHASE,
        customer: {
            id: 2,
            first_name: "Test customer",
            last_name: "2",
            category: "Customer",
            ssn: "123456789",
            emails: [],
            addresses: [],
            phone_numbers: [],
            notes: "Customer notes",
            preferences: "Customer preferences"
        } as CustomerType
    },
    {
        id: 43,
        title: "Test job offer",
        description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies.",
        status: JobOfferStatus.CREATED,
        customer: {
            id: 1,
            first_name: "Test customer",
            last_name: "1",
            category: "Customer",
            ssn: "123456789",
            emails: [],
            addresses: [],
            phone_numbers: [],
            notes: "Customer notes",
            preferences: "Customer preferences"
        } as CustomerType
    },
    {
        id: 4,
        title: "Test job offer",
        description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies.",
        status: JobOfferStatus.CANDIDATE_PROPOSAL,
        customer: {
            id: 2,
            first_name: "Test customer",
            last_name: "2",
            category: "Customer",
            ssn: "123456789",
            emails: [],
            addresses: [],
            phone_numbers: [],
            notes: "Customer notes",
            preferences: "Customer preferences"
        } as CustomerType
    },
    {
        id: 5,
        title: "Test job offer",
        description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies.",
        status: JobOfferStatus.CONSOLIDATED,
        customer: {
            id: 1,
            first_name: "Test customer",
            last_name: "1",
            category: "Customer",
            ssn: "123456789",
            emails: [],
            addresses: [],
            phone_numbers: [],
            notes: "Customer notes",
            preferences: "Customer preferences"
        } as CustomerType
    },
    {
        id: 6,
        title: "Test job offer",
        description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies.",
        status: JobOfferStatus.DONE,
        customer: {
            id: 2,
            first_name: "Test customer",
            last_name: "2",
            category: "Customer",
            ssn: "123456789",
            emails: [],
            addresses: [],
            phone_numbers: [],
            notes: "Customer notes",
            preferences: "Customer preferences"
        } as CustomerType
    },
    {
        id: 7,
        title: "Test job offer",
        description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies.",
        status: JobOfferStatus.DONE,
        customer: {
            id: 2,
            first_name: "Test customer",
            last_name: "2",
            category: "Customer",
            ssn: "123456789",
            emails: [],
            addresses: [],
            phone_numbers: [],
            notes: "Customer notes",
            preferences: "Customer preferences"
        } as CustomerType
    },
    {
        id: 8,
        title: "Test job offer",
        description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies.",
        status: JobOfferStatus.ABORTED,
        customer: {
            id: 2,
            first_name: "Test customer",
            last_name: "2",
            category: "Customer",
            ssn: "123456789",
            emails: [],
            addresses: [],
            phone_numbers: [],
            notes: "Customer notes",
            preferences: "Customer preferences"
        } as CustomerType
    },
    {
        id: 9,
        title: "Test job offer",
        description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies.Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies. Nullam nec purus nec nunc tincidunt ultricies.",
        status: JobOfferStatus.ABORTED,
        customer: {
            id: 2,
            first_name: "Test customer",
            last_name: "2",
            category: "Customer",
            ssn: "123456789",
            emails: [],
            addresses: [],
            phone_numbers: [],
            notes: "Customer notes",
            preferences: "Customer preferences"
        } as CustomerType
    },
] as JobOfferHeader[];