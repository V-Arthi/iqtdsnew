import React, { useState, useEffect } from "react";
import axios from "axios";
import { Button, message, Select } from "antd";
import { PlusOutlined, SyncOutlined } from "@ant-design/icons";
const config = require("../config.json");
const endpoint = process.env.REACT_APP_API_URL;

const types = ["claims", "members", "providers", "Medical", "Hospital", "Dental"];
const filterMap = {
  claims: "claimFilters",
  members: "memberFilters",
  providers: "providerFilters",
  Medical: "claimFilters",
  Hospital: "claimFilters",
  Dental: "claimFilters",
};

function ComboBox({ label, list, value, onChange, disabled }) {
  return (
    <div className="filter_form_item">
      <label htmlFor={label}>{label}</label>
      {/* <select
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
      </select> */}
      <Select
        showSearch
        name={label}
        value={value}
        popupMatchSelectWidth={false}
        onChange={onChange}
        disabled={disabled}
        filterOption={(input, option) => (option?.label ?? '').toLowerCase().includes(input.toLowerCase())}
      >
        {list.map((li, index) => (
          <option
            key={index}
            value={li}
            label={li}
          >{li}</option>
        ))}
      </Select>
    </div>
  );
}

function Input({ label, value, onChange }) {
  return (
    <div className="filter_form_item">
      <label htmlFor={label}>{label}</label>
      <input className="filter-form-input" name={label} type="text" value={value} onChange={onChange} />
    </div>
  );
}

function FilterForm({ test, setTest, filterType }) {
  const [filterItem, setFilterItem] = useState({
    field: "",
    operator: "=",
    value: "",
  });
  const [meta, setMeta] = useState([]);

  useEffect(() => {
    if (types.indexOf(filterType) >= 0) {
      axios
        .get(`${endpoint}/facets/${filterType}/meta`)
        .then((res) => setMeta(["", ...res.data]))
        .catch((err) => console.log(err));
    }
  }, [filterType]);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (filterItem.field && filterItem.operator && filterItem.value) {
      const filtername = filterMap[filterType];
      const existing_filters = test[filtername];

      if (
        existing_filters.filter(
          (f) =>
            f.field === filterItem.field &&
            (f.operator === filterItem.operator || f.operator === "=")
        ).length > 0
      ) {
        message.warning("This filter already exists");
        return;
      } else {
        setTest({ ...test, [filtername]: [...test[filtername], filterItem] });
      }

      setFilterItem({ field: "", operator: "=", value: "" });
    }
  };

  const handleReset = () => {
    setFilterItem({ field: "", operator: "=", value: "" });
  };
 
  return (
    types.indexOf(filterType) >= 0 && (
      <form
        onSubmit={handleSubmit}
        className="filter_form"
        onReset={handleReset}
      >
        <ComboBox
          label="field"
          list={meta}
          value={filterItem.field}
          onChange={(e) =>
            setFilterItem({ ...filterItem, field: e })
          }
          required
        />
        <ComboBox
          label="operator"
          list={config.operators}
          value={filterItem.operator}
          onChange={(e) =>
            setFilterItem({ ...filterItem, operator: e })
          }
          required
        />
        <Input
          label="value"
          value={filterItem.value}
          onChange={(e) =>
            setFilterItem({ ...filterItem, value: e.target.value })
          }
          required
        />
        <div className="btn_group" style={{ marginLeft: "auto", marginTop: 5 }}>
          <Button
            icon={<PlusOutlined />}
            htmlType="submit"
            className="btn add_btn"
            type="primary"
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
    )
  );
}

export default FilterForm;
