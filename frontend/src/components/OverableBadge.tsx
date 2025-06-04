import React from 'react';
import {Badge, OverlayTrigger, Tooltip, TooltipProps} from "react-bootstrap";
import {JSX} from 'react/jsx-runtime';

function OverableBadge(props: {
    content: string | React.ReactNode;
    tooltip: string;
    className?: string;
    bg?: string;
    pill?: boolean;
    style?: React.CSSProperties;
}) {
    const renderTooltip = (p: JSX.IntrinsicAttributes & TooltipProps & React.RefAttributes<HTMLDivElement>) => (
        <Tooltip id="badge-tooltip" {...p}>
            {props.tooltip}
        </Tooltip>
    );
    
    return (
        <OverlayTrigger
            placement={"top"}
            overlay={renderTooltip}
            delay={{ show: 250, hide: 400 }}
        >
            <Badge pill={props.pill} className={props.className} style={props.style} bg={props.bg ? props.bg : "primary"}>
                {props.content}
            </Badge>
        </OverlayTrigger>
    );
}

export default OverableBadge;