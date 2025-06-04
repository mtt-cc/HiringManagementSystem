
export type MessageType ={
    id: number;
    subject: string;
    sender: string;
    channel: Channel;
    body: string;
    date: string;
    actual_message_state: string;
    attachments: AttachmentType[];
}

export type AttachmentType ={
    id: number;
    name: string;
    type: string;
    size: number;
    date: string;
}

// frontend/src/types/Message.ts

export enum Channel {
    Email = 'Email',
    PhoneNumber = 'PhoneNumber'
}

export const channelToString = (channel: Channel): string => {
    switch (channel) {
        case Channel.Email:
            return 'Email';
        case Channel.PhoneNumber:
            return 'Telephone';
        default:
            return 'Unknown';
    }
};