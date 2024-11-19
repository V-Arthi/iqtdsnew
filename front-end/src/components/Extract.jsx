import React, { useEffect, useState } from "react";
import { Button, Divider, Empty, message, Typography, Spin } from "antd";
import OverridesPerExtract from "./OverridesPerExtract";
import axios from "axios";
import EDIOutput from "./EDIOutput";

const { Paragraph, Title, Text } = Typography;

const endpoint = process.env.REACT_APP_API_URL;

function ComboBox({ label, list, value, onChange, disabled }) {
  return (
    <div className="custom_field custom_combo">
      <label htmlFor={label}>{label}</label>
      <select
        value={value}
        name={label}
        onChange={onChange}
        disabled={disabled}
      >
        {list.map((li, index) => (
          <option key={index} value={li}>
            {li}
          </option>
        ))}
      </select>
    </div>
  );
}

function Extract({ runExtract, members, providers,run }) {
  const [overrides, setOverrides] = useState([]);
  const [extract, setExtract] = useState();
  const [memberID, setMemberID] = useState("");
  const [providerID, setProviderID] = useState("");

  const [fileLoad, setFileLoad] = useState(false);

  const memberList = ["", ...members];
  const providerList = ["", ...providers];

  const [dataMocked, setDataMocked] = useState({});

  useEffect(() => {
    setExtract(runExtract.extract || null);

    if (runExtract.overrides && runExtract.overrides.length > 0) {
      setOverrides(
        runExtract.overrides.map((o) => ({
          fieldName: o.ediMap.fieldName,
          fieldValue: o.value,
          txnNumber: o.txnNumber,
        }))
      );
    }
  }, [runExtract]);

  const generateEDI = () => {
    const data = { runExtractMapID: runExtract.id, memberID, providerID };
    setFileLoad(true);
    axios
      .post(`${endpoint}/data/create-edi`, data)
      .then((res) => {
        console.log(res.data);
        setDataMocked(res.data);
        setFileLoad(false);
      })
      .catch((err) => {
        message.error("error mocking up data");
        console.log(err);
        setFileLoad(false);
      });
  };

  return extract ? (
    <>
      {extract.extractData ? (
        <>
          <Text type="secondary">{`${extract.dataId} | ${extract.env}`}</Text>
          <Title level={5}>{extract.fileName}</Title>
          <Paragraph code copyable>
            {extract.extractData}
          </Paragraph>

          <OverridesPerExtract
            setOverrides={setOverrides}
            overrides={overrides}
            runExtID={runExtract.id}
            run={run}
          />

          <div className="member_provider_pick">
            <ComboBox
              label="Member"
              list={memberList}
              value={memberID}
              onChange={(e) => setMemberID(e.target.value)}
            />
            <ComboBox
              label="Provider"
              list={providerList}
              value={providerID}
              onChange={(e) => setProviderID(e.target.value)}
            />
            <div className="tip">
              <span>Please select member & provider overrides from here</span>
              <span>
                Member & provider ID's are displayed from run's result grid
              </span>
            </div>
          </div>

          <Button onClick={generateEDI} type="primary" disabled={fileLoad}>
            Generate EDI
          </Button>

          {fileLoad && <Spin tip="file is being mocked up.. please wait" />}

          {dataMocked && dataMocked.fileContent && (
            <>
              <Divider>Output File</Divider>
              <EDIOutput data={dataMocked} />
            </>
          )}
        </>
      ) : (
        <Empty description={<span>{extract.extractLogs}</span>} />
      )}
      <Divider />
    </>
  ) : (
    <></>
  );
}

export default Extract;
