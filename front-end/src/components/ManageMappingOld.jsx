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
    title: "EDI Mapping",
    dataIndex: "segmentRef",
    key: "segmentRef",
    ...getColumnSearchProps("segmentRef"),
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

function ManageMapping() {
  const [mappings, setMapppings] = useState([]);
  const [edit, setEdit] = useState({
    fieldName: "",
    segmentRef: "",
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
    if (!(edit.fieldName && edit.segmentRef)) return;

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
    setEdit({ fieldName: "", segmentRef: "" });
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
        <span>Mappings are displayed as </span>
        <span className="syntax">loopid_segmentid+elementid-elementsubid</span>
      </li>
      <li>
        <span>Mulitple mappings for same field displayed as </span>
        <span className="syntax">mapping1,mapping2,etc.,</span>
      </li>
      <li>
        <span>This or that mappings are displayed as </span>
        <span className="syntax">mapping1~mapping2~etc.,</span>
      </li>
      <li>
        <span>element sub id is optional</span>
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
            value={edit.segmentRef}
            name="segmentRef"
            placeholder="EDI Mapping"
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
            disabled={!(edit.fieldName && edit.segmentRef)}
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
      <Divider />
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
                      segmentRef: d.segmentRef,
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

export default ManageMapping;
