export function min(a: number, b: number): number {
    return a < b ? a : b;
}

export function highlightText(text: string, search: string) {
    if (!search) return text; // Return the original text if no search term

    const regex = new RegExp(`(${search})`, 'gi'); // Create a regex to match the search term
    const parts = text.split(regex); // Split the text by the search term

    return (
        <>
            {
                parts.map((part, index) => (
                    <span
                        key={index}
                        style={{backgroundColor: part.toLowerCase() === search.toLowerCase() ? 'yellow' : ''}}
                    >{part}</span>
                ))
            }
        </>
    );
};

export function capitalizeFirst(text: string) {
    return text.charAt(0).toUpperCase() + text.toUpperCase().slice(1);
}

export function capitalize(text: string) {
    const words = text.split(' ').map(capitalizeFirst);
    return words.join(' ');
}

export const TelephoneRegex = /^\+?[0-9]{1,3}(\s|\-)?\(?\d{1,4}\)?(\s|\-)?\d{1,4}(\s|\-)?\d{1,4}(\s|\-)?\d{1,4}$/;      // SAME AS BACKEND

// Utility function to format date
export function formatDate (dateString: string) {
    const date = new Date(dateString);
    const formattedDate = date.toLocaleDateString('en-GB'); // dd-mm-yyyy
    const formattedTime = date.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' }); // hh:mm
    return {date:formattedDate, time:formattedTime};
}

export function formatTimestamp(timestamp: string): string {
    const date = new Date(timestamp);

    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0'); // Months are 0-based
    const year = date.getFullYear();

    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');

    return `${day}/${month}/${year} - ${hours}:${minutes}`;
}

export function formatBytes(bytes: number): string {
    if (bytes < 1024) {
        return `${bytes} B`;
    } else if (bytes < 1024 ** 2) {
        return `${(bytes / 1024).toFixed(2)} KB`;
    } else if (bytes < 1024 ** 3) {
        return `${(bytes / 1024 ** 2).toFixed(2)} MB`;
    } else {
        return `${(bytes / 1024 ** 3).toFixed(2)} GB`;
    }
}

export const FREE_TEXT_MAX_LENGTH = 3000;
export const TEXT_MAX_LENGTH = 240;