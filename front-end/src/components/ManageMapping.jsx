import React, { useState, useEffect, useContext } from "react";
import axios from "axios";
import {
  Table,
  Button,
  Input,
  Space,
  message,
  Layout,
  Divider,
  Popover,
  Modal,
  Checkbox,
} from "antd";
import {
  SearchOutlined,
  EditFilled,
  QuestionCircleFilled,
  PlusOutlined,
  CloseCircleTwoTone,
  DeleteFilled,
} from "@ant-design/icons";
import { UserContext } from "../Contexts/UserContext";
import { useHistory } from "react-router-dom";

import "../styles/manage-mapping.css";

const endpoint = process.env.REACT_APP_API_URL;

const { Header, Content } = Layout;

const getColumnSearchProps = (dataIndex) => ({
  filterDropdown: ({
    setSelectedKeys,
    selectedKeys,
    confirm,
    clearFilters,
    searchInput,
  }) => (
    <div style={{ padding: 8 }}>
      <Input
        ref={(node) => (searchInput = node)}
        placeholder={`Search ${dataIndex}`}
        value={selectedKeys[0]}
        onChange={(e) =>
          setSelectedKeys(e.target.value ? [e.target.value] : [])
        }
        onPressEnter={() => handleSearch(selectedKeys, confirm, dataIndex)}
        style={{ width: 188, marginBottom: 8, display: "block" }}
      />
      <Space>
        <Button
          type="primary"
          onClick={() => handleSearch(selectedKeys, confirm, dataIndex)}
          icon={<SearchOutlined />}
          size="small"
          style={{ width: 90 }}
        >
          Search
        </Button>
        <Button
          onClick={() => handleReset(clearFilters)}
          size="small"
          style={{ width: 90 }}
        >
          Reset
        </Button>
      </Space>
    </div>
  ),
  filterIcon: (filtered) => (
    <SearchOutlined style={{ color: filtered ? "#1890ff" : undefined }} />
  ),
  onFilter: (value, record) =>
    record[dataIndex]
      ? record[dataIndex].toString().toLowerCase().includes(value.toLowerCase())
      : "",
});

const handleSearch = (selectedKeys, confirm, dataIndex) => {
  confirm();
};

const handleReset = (clearFilters) => {
  clearFilters();
};

const columns = [
  {
    title: "Field Name",
    dataIndex: "fieldName",
    key: "fieldName",
    ...getColumnSearchProps("fieldName"),
  },
  {
    title: "Claim Type",
    dataIndex: "claimType",
    key: "claimType",
    ...getColumnSearchProps("claimType"),
  },
  {
    title: "Loop",
    dataIndex: "loop",
    key: "loop",
    ...getColumnSearchProps("loop"),
  },
  {
    title: "Loop Repeat",
    dataIndex: "loopRepeat",
    key: "loopRepeat",
    ...getColumnSearchProps("loopRepeat"),
  },
  {
    title: "Segment",
    dataIndex: "segment",
    key: "segment",
    ...getColumnSearchProps("segment"),
  },
  {
    title: "Segment Repeat",
    dataIndex: "segmentRepeat",
    key: "segmentRepeat",
    ...getColumnSearchProps("segmentRepeat"),
  },
  {
    title: "Element",
    dataIndex: "element",
    key: "element",
    ...getColumnSearchProps("element"),
  },
  {
    title: "Sub Element",
    dataIndex: "subElement",
    key: "subElement",
    ...getColumnSearchProps("subElement"),
  },
  {
    title: "Mockup Eligible",
    dataIndex: "valueOverridable",
    key: "valueOverridable",
    render: (valueOverridable) => (
      <Checkbox checked={valueOverridable} disabled />
    ),
  },
  { title: "", dataIndex: "action", key: "action" },
];

function NewManageMapping() {
  const [mappings, setMapppings] = useState([]);
  const [edit, setEdit] = useState({
    fieldName: "",
    claimType: "",
    loop:"",
    loopRepeat:"",
    segment:"",
    segmentRepeat:"",
    element:"",
    subElement:"",
    valueOverridable: false,
  });
  const [mode, setMode] = useState("edit");
  const history = useHistory();
  const { authUser } = useContext(UserContext);

  const loadData = () => {
    axios
      .get(`${endpoint}/config/edi-mapping/all`)
      .then((res) => setMapppings(res.data))
      .catch((err) => message.error("error while loading mapping data"));
  };

  useEffect(() => {
    if (!authUser.authenticated) {
      history.push({ pathname: "/" });
    }

    loadData();
  }, [authUser, history]);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!(edit.fieldName && edit.claimType && edit.loop && edit.loopRepeat &&edit.segment && edit.segmentRepeat && edit.element && edit.subElement)) return;

    if (mode === "edit") {
      axios
        .put(`${endpoint}/config/edi-mapping/update/${edit.fieldName}`, edit)
        .then((res) => {
          message.success(res.data);
          handleReset();
          loadData();
        })
        .catch((err) => message.error(err.res.data));
    } else if (mode === "add") {
      axios
        .post(`${endpoint}/config/edi-mapping/add`, edit)
        .then((res) => {
          message.success(res.data);
          handleReset();
          loadData();
        })
        .catch((err) => message.error(err.res.data));
    }
  };

  const handleReset = () => {
    setEdit({ fieldName: "", 
      claimType: "",
      loop:"",
      loopRepeat:"",
      segment:"",
      segmentRepeat:"",
      element:"",
      subElement:"", });
    resetMode();
  };

  const resetMode = () => {
    setMode("edit");
  };

  const handleDelete = (fieldName) => {
    Modal.confirm({
      title: `Delete Mapping For '${fieldName}' ?`,
      okText: "Yes",
      cancelText: "No",
      content: "This action cannot be undone.",
      icon: <CloseCircleTwoTone />,
      centered: true,
      mask: true,
      maskClosable: true,
      onOk: () => {
        axios
          .delete(`${endpoint}/config/edi-mapping/delete/${fieldName}`)
          .then((res) => {
            message.success(res.data);
            loadData();
          })
          .catch((err) => message.err(err.res.data));
      },
    });
  };

  const dataSyntax = (
    <ul className="data_explanation">
      <li>
        <span>Mappings are from ASC X12 Standards for Electronic Data Interchange </span>
      </li>
      <li>
        <span>ASC X12N/005010X222 for 837P </span>
      </li>
      <li>
        <span>ASC X12N/005010X223 for 837I</span>
      </li>
      <li>
        <span>ASC X12N/005010X224 for 837D</span>
      </li>
    </ul>
  );

  const handleChange = (e) => {
    const { name, value } = e.target;
    const edit__clone = { ...edit };
    edit__clone[name] = value;
    setEdit(edit__clone);
  };

  const handleCheckChange = (e) => {
    const { name, checked } = e.target;
    const edit__clone = { ...edit };
    edit__clone[name] = checked;
    setEdit(edit__clone);
  };

  return (
    <Layout className="manage-mapping">
      <Header className="manage-mapping-header">
        <form
          onSubmit={handleSubmit}
          onReset={handleReset}
          className="manage_mapping_form"
        >
          <Input
            value={edit.fieldName}
            name="fieldName"
            placeholder="Field Name"
            className="mapping_form_field"
            disabled={mode === "edit"}
            onChange={(e) => handleChange(e)}
          />
          <Input
            value={edit.claimType}
            name="claimType"
            placeholder="Claim Type"
            className="mapping_form_field"
            onChange={(e) => handleChange(e)}
          />
          <Input
            value={edit.loop}
            name="loop"
            placeholder="Loop"
            className="mapping_form_field"
            onChange={(e) => handleChange(e)}
          />
          <Input
            value={edit.loopRepeat}
            name="loopRepeat"
            placeholder="Loop Repeat"
            className="mapping_form_field"
            onChange={(e) => handleChange(e)}
          />
          <Input
            value={edit.segment}
            name="segment"
            placeholder="Segment"
            className="mapping_form_field"
            onChange={(e) => handleChange(e)}
          />
          <Input
            value={edit.segmentRepeat}
            name="segmentRepeat"
            placeholder="Segment Repeat"
            className="mapping_form_field"
            onChange={(e) => handleChange(e)}
          />
          <Input
            value={edit.element}
            name="element"
            placeholder="Element"
            className="mapping_form_field"
            onChange={(e) => handleChange(e)}
          />
          <Input
            value={edit.subElement}
            name="subElement"
            placeholder="Sub Element"
            className="mapping_form_field"
            onChange={(e) => handleChange(e)}
          />
          <div className="mapping_form_field check">
            <input
              type="checkbox"
              checked={edit.valueOverridable}
              id="mockup_elig_flag"
              name="valueOverridable"
              onChange={(e) => handleCheckChange(e)}
            />
            <label htmlFor="mockup_elig_flag">Mockup Eligible?</label>
          </div>
          {/* <Checkbox checked={edit.valueOverridable} name="valueOverridable" className="mapping_form_field check" onChange={e=>handleCheckChange(e)} >Mockup Eligible?</Checkbox> */}
          <Button
            type="primary"
            htmlType="submit"
            className="mapping_form_button"
            disabled={!(edit.fieldName && edit.claimType && edit.loop && edit.loopRepeat &&edit.segment && edit.segmentRepeat && edit.element && edit.subElement)}
          >
            {mode === "edit" ? "Update" : "Save"}
          </Button>
          <Button htmlType="reset" className="mapping_form_button">
            Clear
          </Button>
        </form>
        <Button
          icon={<PlusOutlined />}
          type="link"
          disabled={mode !== "edit"}
          onClick={(e) => setMode("add")}
          style={{ marginLeft: 20 }}
        >
          Add New
        </Button>
        <Popover content={dataSyntax} title="Data Syntax">
          <Button
            style={{ marginLeft: "auto" }}
            icon={<QuestionCircleFilled style={{ fontSize: 20 }} />}
            type="link"
          />
        </Popover>
      </Header>
      <Divider>EDI Mapping</Divider>
      <Content className="manage-mapping-content">
        <Table
          className="mappings-table"
          bordered
          small
          columns={columns}
          dataSource={mappings.map((d, index) => ({
            ...d,
            key: index,
            action: (
              <>
                <Button
                  type="link"
                  icon={<EditFilled />}
                  onClick={(e) =>
                    setEdit({
                      fieldName: d.fieldName,
                      claimType: d.claimType,
                      loop: d.loop,
                      loopRepeat: d.loopRepeat,
                      segment: d.segment,
                      segmentRepeat: d.segmentRepeat,
                      element: d.element,
                      subElement: d.subElement,
                      valueOverridable: d.valueOverridable,
                    })
                  }
                />
                <Button
                  type="link"
                  onClick={(e) => handleDelete(d.fieldName)}
                  style={{ color: "crimson", marginLeft: 10 }}
                  icon={<DeleteFilled />}
                />
              </>
            ),
          }))}
        />
      </Content>
    </Layout>
  );
}

export default NewManageMapping;
