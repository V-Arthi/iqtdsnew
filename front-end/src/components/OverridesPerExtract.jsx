import React, { useState, useEffect } from "react";
import axios from "axios";
import { message, Button, Divider, Typography, Popover } from "antd";
import { QuestionCircleFilled, PlusOutlined, SyncOutlined } from "@ant-design/icons";
import Override from "./Override";
import { v4 as uuid } from "uuid";


const endpoint = process.env.REACT_APP_API_URL;

const empty_val = { fieldName: "", txnNumber: "", fieldValue: "" };

const { Title } = Typography;

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

function Input({ label, value, onChange }) {
  return (
    <div className="custom_field custom_input">
      <label htmlFor={label}>{label}</label>
      <input name={label} type="text" value={value} onChange={onChange} />
    </div>
  );
}
const dataSyntax = (
  <ul className="data_explanation">
    <li>
      <span>Date <b>MM/dd/yyyy</b></span>
    </li>
  </ul>
);

function OverridesPerExtract({ overrides, setOverrides, runExtID, run }) {
  const [mapping, setMapping] = useState([]);
  const [currVal, setCurrVal] = useState(empty_val);

  useEffect(() => {
    const claimType = run.testCondition.claimType;
    axios
      .get(`${endpoint}/config/edi-mapping/mockup-eligible/${claimType}`)
      // .get(`${endpoint}/config/edi-mapping/mockup-eligible`)
      .then((res) => setMapping(["", ...res.data]))
      .catch((err) => {
        message.error("error while getting edi mapping fields");
        console.log(err);
      });
  }, [run]);

  // useEffect(() => {
  //   axios
  //     .get(`${endpoint}/config/edi-mapping/mockup-eligible`)
  //     .then((res) => setMapping(["", ...res.data]))
  //     .catch((err) => {
  //       message.error("error while getting edi mapping fields");
  //       console.log(err);
  //     });
  // }, []);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (currVal.fieldName && currVal.fieldValue) {
      setOverrides((prevOverrides) => [...prevOverrides, currVal]);
      setCurrVal(empty_val);
    }
  };

  const handleReset = () => {
    setCurrVal(empty_val);
  };

  const removeOverride = (override) => {
    const overrides_clone = [...overrides];
    setOverrides(overrides_clone.filter((o) => o !== override));
  };

  const saveOverrides = () => {
    const overrides_mutated = overrides.map((o) => ({
      ediMap: mapping.find((m) => m.fieldName === o.fieldName),
      txnNumber: o.txnNumber,
      value: o.fieldValue,
    }));
    axios
      .post(
        `${endpoint}/data/run-extract/${runExtID}/attach-overrides`,
        overrides_mutated
      )
      .then((res) => message.success(res.data))
      .catch((err) => message.error(JSON.stringify(err)));
  };

  return (
    <div className="extract__overrides">
      <Title level={4} style={{ marginBottom: 20, color: "#777" }}>
        Overrides
      </Title>
      <form
        onSubmit={(e) => handleSubmit(e)}
        onReset={handleReset}
        className="overrides__form"
      >
        <ComboBox
          label="Field"
          onChange={(e) =>
            setCurrVal({ ...currVal, fieldName: e.target.value })
          }
          list={mapping.map((m) => m.fieldName)}
          value={currVal.fieldName}
        />
        <Input
          label="Txn #/Line #"
          onChange={(e) =>
            setCurrVal({ ...currVal, txnNumber: e.target.value })
          }
          value={currVal.txnNumber}
        />
        <Input
          label="Value"
          onChange={(e) =>
            setCurrVal({ ...currVal, fieldValue: e.target.value })
          }
          value={currVal.fieldValue}
        />

        <div className="btn_group">
          <Popover content={dataSyntax} title="Data Syntax">
            <Button
              style={{ marginLeft: "auto" }}
              icon={<QuestionCircleFilled style={{ fontSize: 20 }} />}
              type="link"
            />
          </Popover>
          <Button
            icon={<PlusOutlined />}
            htmlType="submit"
            className="btn add_btn"
            type="primary"
            style={{ marginRight: 10 }}
          >
            Add
          </Button>
          <Button
            icon={<SyncOutlined />}
            htmlType="reset"
            className="btn reset_btn"
            type="ghost"
          >
            Reset
          </Button>
        </div>
      </form>

      {overrides && overrides.length > 0 && (
        <>
          <Divider />
          <ul className="overrides__list">
            {overrides.map((o) => (
              <Override
                override={o}
                key={uuid()}
                removeFn={(e) => removeOverride(o)}
              />
            ))}
          </ul>
        </>
      )}
      <Button className="btn_save_overrides" onClick={saveOverrides}>
        Save Overrides
      </Button>
    </div>
  );
}

export default OverridesPerExtract;
