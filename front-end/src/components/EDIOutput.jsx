import React from "react";
import { Button, Collapse, Typography } from "antd";

const { Paragraph, Title } = Typography;
const { Panel } = Collapse;

const createFile = (fileName, data) => {
  const f = new Blob([data], {
    type: "text/plain",
  });
  const x12File = window.URL.createObjectURL(f);
  const el = document.createElement("a");
  el.setAttribute("id", "download_link");
  el.href = x12File;
  el.download = fileName;
  el.click();
  el.remove();
};

function EDIOutput({ data }) {
  return (
    <div class="output">
      <Title level={5}>
        {data.fileName}
        <span>
          <Button
            type="link"
            onClick={() => createFile(data.fileName, data.fileContent)}
          >
            Download as File
          </Button>
        </span>
      </Title>
      <Paragraph code copyable>
        {data.fileContent}
      </Paragraph>

      <Collapse accordion>
        <Panel
          header="Mockup Logs"
          key={`${data.fileName}_log`}
          className="logs_box"
        >
          <div
            className="logs"
            style={{
              backgroundColor: "#000",
              color: "#fff",
              fontStyle: "italic",
              padding: "5px",
              fontSize: "12px",
            }}
          >
            {data.mockupLogs.split("\n").map((line, index) => (
              <span key={`edi_line_${index}`}>
                {line}
                <br />
              </span>
            ))}
          </div>
        </Panel>
      </Collapse>
    </div>
  );
}

export default EDIOutput;
