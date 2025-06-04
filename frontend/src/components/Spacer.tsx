interface SpacerProps {
    height: string | number;
    borderBottom?: string;
}

function Spacer(props: SpacerProps) {
    return (
        <div style={{
            height: props.height,
            borderBottom: props.borderBottom,
            marginBottom: !props.borderBottom ? "0" : ".5rem"
        }}/>
    );
}

export default Spacer;