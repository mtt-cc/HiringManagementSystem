import {Badge, Stack} from "react-bootstrap";

export type BadgeEntry = {
    key: string,
    text: string,
}

interface BadgeListProps {
    badges: BadgeEntry[],
    bg?: string,
    pill?: boolean,
    left?: boolean,
}

interface BasicBadgeProps {
    text: string,
    bg?: string,
    pill?: boolean,
}

function BadgeList(props: BadgeListProps) {
    return (
        <Stack direction="horizontal" gap={2} className={`d-flex flex-wrap ${props.left ? "justify-content-start" : "justify-content-center"} w-100`}>
            {
                props.badges.map((badge, index) => {
                    return (
                        <BasicBadge
                            key={index}
                            text={badge.text}
                            bg={props.bg}
                            pill={props.pill}
                        />
                    );
                })
            }
        </Stack>
    );
}

export function BasicBadge(props: BasicBadgeProps) {
    return (
        <Badge
            bg={props.bg ? props.bg : "primary"}
            pill={props.pill}
            style={{paddingLeft: "0.75rem", paddingRight: "0.75rem", paddingTop: "0.5rem", paddingBottom: "0.5rem"}}
        >
            {props.text}
        </Badge>
    );
}


export default BadgeList;