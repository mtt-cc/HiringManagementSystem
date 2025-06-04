import {Accordion, Col, Row} from "react-bootstrap";

export type CollapsableItemListEntryType = {
    id: number;
    title: string;
    content: string;
}

interface CollapsableItemListProps {
    items: CollapsableItemListEntryType[];
    title: string;
}

function CollapsableItemListEntry(props: {
    item: CollapsableItemListEntryType;
    titleSize: number | undefined;
    last: boolean;
}) {
    return (
        <Row className="p-3" style={{borderBottom: props.last ? "none" : ".1rem lightgray solid"}}>
            <Col className={props.titleSize === undefined ? "col-4" : `col-${props.titleSize}`}>
                <p className="fw-bold m-0">{props.item.title}</p>
            </Col>
            <Col>
                <p className="m-0">{props.item.content}</p>
            </Col>
        </Row>
    );
}

function CollapsableItemList(props: CollapsableItemListProps) {
    return (
        <Accordion defaultValue="0">
            <Accordion.Item eventKey="0">
                <Accordion.Header>{props.title}</Accordion.Header>
                <Accordion.Body>
                    {props.items.map((item, index) => (
                        <CollapsableItemListEntry key={item.id} item={item} titleSize={2} last={index===props.items.length-1}/>
                    ))}
                </Accordion.Body>
            </Accordion.Item>
        </Accordion>
    );
}

export default CollapsableItemList;