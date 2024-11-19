import React, {  useEffect } from "react";

import { Input } from "antd";
import { QuestionCircleFilled } from "@ant-design/icons";
import { Button, Popover} from "antd";

const config = require("../config.json");
// const endpoint = process.env.REACT_APP_API_URL;
const claimTypes = config.claim_types;
const claimSource = config.claim_input_types;

function Select({ label, choices, value, onChange }) {
  return (
    <span className="ant-input-group-wrapper">
      <span className="ant-input-wrapper ant-input-group">
        <span className="ant-input-group-addon">{label}</span>
        <select className="ant-input" value={value} onChange={onChange}>
          {choices.map((li, index) => (
            <option key={index} value={li}>
              {li}
            </option>
          ))}
        </select>
      </span>
    </span>
  );
}


const dataSyntax = (
  <ul className="data_explanation">
    <li>
      <span>Value for SUBSTR should be like: <b>1,5 in A4351,A4562,A4566</b></span>
    </li>
    <li>
      <span>Value for IN & NOT IN should be like: <b>A4351,A4562,A4566</b></span>
    </li>
    <li>
      <span>Value for LIKE & NOT LIKE should be like: <b>A4351</b> or <b>%A4562</b> or <b>A4562%</b> or <b>%A4562%</b></span>
    </li>
    <li>
      <span></span>
    </li>
  </ul>
);


function TestHeader({ test, setTest, className }) {
  

  useEffect(() => {
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    const test_clone = { ...test };
    test_clone[name] = value;
    setTest(test_clone);
  };

  return (
    <header className={className}>
      
        <Input
          addonBefore="Test ID"
          placeholder="Test Id"
          value={test.testId}
          disabled
          style={{width: '20%'}}
        />
        <Input
          addonBefore="Data Condition Name"
          placeholder="Data Condition Name"
          name="testName"
          value={test.testName}
          onChange={handleChange}
          style={{width: '50%'}}
        />
        <Select
          choices={claimSource}
          label="Claim Input Type"
          name="claimInputType"
          value={test.claimInputType}
          onChange={(e) => setTest({ ...test, claimInputType: e.target.value })}
          style={{width: '40%'}}
        />
        <Select
          choices={claimTypes}
          label="Claim Type"
          name="claimType"
          value={test.claimType}
          onChange={(e) => setTest({ ...test, claimType: e.target.value })}
          style={{width: '40%'}}
        />
        
        <Popover content={dataSyntax} title="Data Syntax">
          <Button
            style={{ marginLeft: "25px", marginTop: "25px" , display:"flex"}}
            icon={<QuestionCircleFilled style={{ fontSize: 20 }} />}
            type="link"
            className="btn_group"
          />
        </Popover>
      
    </header>
  );
}

export default TestHeader;
