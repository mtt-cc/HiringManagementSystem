const URL = "http://localhost:8080"

export const Configuration = {
    url: URL,
    routes: {
        me: URL + "/user/me",
        crm: {
            get: {
                // CONTACTS ---------------------------------------------------
                contacts: (
                    page: number,
                    size: number,
                    category: string,
                    country: string,
                    city: string,
                    first_name: string,
                    last_name: string,
                    ssn: string
                ) => {
                    let url = URL + "/API/v1/crm/contacts?page=" + page + "&size=" + size;
                    if (category !== "all") {
                        url += "&category=" + category;
                    }
                    if (country !== "") {
                        url += "&country=" + country;
                    }
                    if (city !== "") {
                        url += "&city=" + city;
                    }
                    if (first_name !== "") {
                        url += "&first_name=" + first_name;
                    }
                    if (last_name !== "") {
                        url += "&last_name=" + last_name;
                    }
                    if (ssn !== "") {
                        url += "&ssn=" + ssn;
                    }
                    return url;
                },
                contact: (id: string) => URL + "/API/v1/crm/contacts/" + id,
                professionalOptions: URL + "/API/v1/crm/contacts/professional-headers",
                customerOptions: URL + "/API/v1/crm/contacts/customer-headers",
                professional: (page: number, size: number, skills: string[], jobOfferId: number, availableOnly: boolean) => {
                    let url = URL + "/API/v1/crm/contacts/professionals?page=" + page + "&size=" + size;
                    url += "&skills_set=" + skills.join(",");
                    url += "&job_offer_id=" + jobOfferId;
                    if (availableOnly) {
                        url += "&available_only=true";
                    }
                    return url;
                },

                // MESSAGES ---------------------------------------------------
                messages: (
                    page: number, 
                    size: number,
                    sorting: string,
                    state: string
                ) => {
                    let url = URL + "/API/v1/crm/messages?page=" + page + "&size=" + size;
                    if (sorting !== "") {
                        url += "&sorting=" + sorting;
                    }
                    if (state !== "") {
                        url += "&state=" + state;
                    }
                    return url;
                },
                message: (id: string) => URL + "/API/v1/crm/messages/" + id,
                messageHistory: (id: string) => URL + "/API/v1/crm/messages/" + id + "/history",

                // JOB OFFERS -------------------------------------------------
                joboffers: (
                    page: number,
                    size: number,
                    states: string[],
                    search: string,
                    customers: number[],
                    professionals: number[],
                    valueLow: number,
                    valueHigh: number
                ) => {
                    let url = URL + "/API/v1/crm/jobOffers?page=" + page + "&size=" + size;
                    if (states.length > 0) {
                        url += "&states=" + states.join(",");
                    }
                    if (search !== "") {
                        url += "&search=" + search;
                    }
                    if (customers.length > 0) {
                        url += "&customers=" + customers.join(",");
                    }
                    if (professionals.length > 0) {
                        url += "&professionals=" + professionals.join(",");
                    }
                    if (valueLow > 0) {
                        url += "&valueLow=" + valueLow;
                    }
                    if (valueHigh > 0) {
                        url += "&valueHigh=" + valueHigh;
                    }
                    return url;
                },
                joboffer: (id: string) => URL + "/API/v1/crm/jobOffers/" + id,
            },
            post: {
                // CONTACTS ---------------------------------------------------
                contact: URL + "/API/v1/crm/contacts",

                // MESSAGES ---------------------------------------------------
                message: URL + "/API/v1/crm/messages",

                // JOB OFFERS -------------------------------------------------
                joboffer: URL + "/API/v1/crm/jobOffers",
                candidateForJO: (jobOfferId: string, professionalId: string) => URL + "/API/v1/crm/jobOffers/" + jobOfferId + "/candidate/" + professionalId,
            },
            put: {
                // CONTACTS ---------------------------------------------------
                contact: (id: string) => URL + "/API/v1/crm/contacts/" + id,

                // MESSAGES ---------------------------------------------------
                messageState: (id: string) => URL + "/API/v1/crm/messages/" + id,
                messagePriority: (id: string) => URL + "/API/v1/crm/messages/" + id + "/priority",

                // JOB OFFERS -------------------------------------------------
                jobofferSkills: (id: string) => URL + "/API/v1/crm/jobOffers/" + id + "/skills",
                jobofferDescription: (id: string) => URL + "/API/v1/crm/jobOffers/" + id + "/description",
                jobofferNotes: (id: string) => URL + "/API/v1/crm/jobOffers/" + id + "/notes",
                jobofferState: (id: string) => URL + "/API/v1/crm/jobOffers/" + id + "/state",
                candidate: (id: string) => URL + "/API/v1/crm/jobOffers/candidate/" + id,
            },
            delete: {
                // CONTACTS ---------------------------------------------------
                contact: (id: string) => URL + "/API/v1/crm/contacts/" + id,
            }
        }
    }
}