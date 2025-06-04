import {ProgressStateBarEntryType} from "../components/ProgressStateBar.tsx";
import {Skill} from "./Skill.ts";
import {CustomerType, ProfessionalType} from "./Contact.ts";

export type JobOfferHeader = {
    id: number;
    title: string;
    description: string;
    status: JobOfferStatus;
    customer: CustomerType;
}

export type JobOffer = JobOfferHeader & {
    duration: number;
    value: number;
    budget: number;
    notes: string;
    history: JobOfferHistoryEntry[];
    skills: Skill[];
    professional: ProfessionalType | null;
    candidates: Candidate[];
}

export type JobOfferHistoryEntry = {
    id: number;
    current_status: JobOfferStatus;
    previous_status?: JobOfferStatus;
    date_time: string;
}

export type Candidate = {
    id: number,
    candidate: ProfessionalType;
    notes: string;
    verified: boolean;
}

export enum JobOfferStatus {
    CREATED = 0,
    SELECTION_PHASE = 1,
    CANDIDATE_PROPOSAL = 2,
    CONSOLIDATED = 3,
    DONE = 4,
    ABORTED = 5,
}

export function stringToJobOfferStatus(status: string): JobOfferStatus {
    switch (status.toLowerCase()) {
        case "created":
            return JobOfferStatus.CREATED;
        case "selection_phase":
            return JobOfferStatus.SELECTION_PHASE;
        case "candidate_proposal":
            return JobOfferStatus.CANDIDATE_PROPOSAL;
        case "consolidated":
            return JobOfferStatus.CONSOLIDATED;
        case "done":
            return JobOfferStatus.DONE;
        case "aborted":
            return JobOfferStatus.ABORTED;
    }
}

export function jobOfferStatusToString(status: JobOfferStatus): string {
    switch (status) {
        case JobOfferStatus.CREATED:
            return "Created";
        case JobOfferStatus.SELECTION_PHASE:
            return "Selection phase";
        case JobOfferStatus.CANDIDATE_PROPOSAL:
            return "Candidate proposal";
        case JobOfferStatus.CONSOLIDATED:
            return "Consolidated";
        case JobOfferStatus.DONE:
            return "Done";
        case JobOfferStatus.ABORTED:
            return "Aborted";
    }
}

export const JobOfferStatusColorMapping = {
    getBg: (i: number) => {
        switch (i) {
            case 0:
                return "blue";
            case 1:
                return "orange";
            case 2:
                return "orange";
            case 3:
                return "green";
            case 4:
                return "green";
            case 5:
                return "red";
        }
    },
    getColor: (i: number) => {
        switch (i) {
            case 0:
                return "white";
            case 1:
                return "#434343";
            case 2:
                return "#434343";
            case 3:
                return "white";
            case 4:
                return "white";
            case 5:
                return "white";
        }
    }
}

export function mapHistoryToStateBarEntry(history: JobOfferHistoryEntry[]): ProgressStateBarEntryType[] {
    const states: ProgressStateBarEntryType[] = [
        {
            index: 0,
            name: jobOfferStatusToString(JobOfferStatus.CREATED),
            date: undefined,
            time: undefined,
            color: "blue",
            half: false,
        },
        {
            index: 1,
            name: jobOfferStatusToString(JobOfferStatus.SELECTION_PHASE),
            date: undefined,
            time: undefined,
            color: "orange",
            half: true,
        },
        {
            index: 2,
            name: jobOfferStatusToString(JobOfferStatus.CANDIDATE_PROPOSAL),
            date: undefined,
            time: undefined,
            color: "orange",
            half: false,
        },
        {
            index: 3,
            name: jobOfferStatusToString(JobOfferStatus.CONSOLIDATED),
            date: undefined,
            time: undefined,
            color: "green",
            half: true,
        },
        {
            index: 4,
            name: jobOfferStatusToString(JobOfferStatus.DONE),
            date: undefined,
            time: undefined,
            color: "green",
            half: false,
        },
    ];

    for (let state of states) {
        const last = getLastOrUndefined(history, state.index);
        if (last) {
            const parsed = parseDateTime(last.date_time);
            state.date = parsed.date;
            state.time = parsed.time;
        }
    }

    return states;
}

export function parseDateTime(dateTimeString : string) {
    const dateObj = new Date(dateTimeString);

    const day = String(dateObj.getDate()).padStart(2, '0');
    const month = String(dateObj.getMonth() + 1).padStart(2, '0'); // getMonth() ritorna 0-indexed
    const year = dateObj.getFullYear();

    const parsedDate = `${day}-${month}-${year}`;

    const hours = String(dateObj.getHours()).padStart(2, '0');
    const minutes = String(dateObj.getMinutes()).padStart(2, '0');
    const gmt = dateObj.getTimezoneOffset() / 60;

    const parsedTime = `${hours}:${minutes} GMT${gmt > 0 ? "+" : ""}${gmt}`;

    return {
        date: parsedDate,
        time: parsedTime
    };
}

function getLastOrUndefined(history: JobOfferHistoryEntry[], state: JobOfferStatus): JobOfferHistoryEntry | undefined {
    const filtered = history.filter(h => h.current_status === state);
    return filtered.length > 0 ? filtered[filtered.length-1] : undefined;
}