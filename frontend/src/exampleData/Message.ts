import {MessageType} from "../types/Message.ts";

export const MessageExampleData = {
    id: 1,
    subject: "SWE Needed",
    sender: "googleceo@google.com",
    channel: "email",
    body: "helo I am ceo of googel and I need a swe to work for me",
    timestamp: "2021-10-10T12:00:00Z",
    status: "RECEIVED",
    list_attachments: [{id: 1}]
} as MessageType;

// this is an example of a list of messages
export const MessageListExampleData = [
    {
        id: 1,
        subject: "SWE Needed",
        sender: "googleceo@google.com",
        channel: "email",
        body: "helo I am ceo of googel and I need a swe to work for me",
        timestamp: "2021-10-10T12:00:00Z",
        status: "RECEIVED"},
    {
        id: 2,
        subject: "message 2",
        sender: "googleceo@google.com",
        channel: "email",
        body: "lorem 2",
        timestamp: "2021-10-10T12:00:00Z",
        status: "RECEIVED"},
    {
        id: 3,
        subject: "message 3",
        sender: "googleceo@google.com",
        channel: "email",
        body: "lorem 3",
        timestamp: "2021-10-10T12:00:00Z",
        status: "READ"}
    ] as MessageType[];