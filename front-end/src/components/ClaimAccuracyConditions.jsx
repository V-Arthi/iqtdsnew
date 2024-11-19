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
import TextArea from "antd/lib/input/TextArea";

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
    title: "Condition Name",
    dataIndex: "condition",
    key: "condition",
    ...getColumnSearchProps("condition"),
  },
  {
    title: "Query",
    dataIndex: "query",
    key: "query",
    ...getColumnSearchProps("query"),
  },
  {
    title: "Active",
    dataIndex: "active",
    key: "active",
    render: (active) => (
      <Checkbox checked={active} enabled="true" />
    ),
  },
  { title: "", dataIndex: "action", key: "action" },
];

function ClaimAccuracyConditions() {
  const [mappings, setMapppings] = useState([]);
  const [edit, setEdit] = useState({
    condition: "",
    query: "",
    active: true,
  });
  const [mode, setMode] = useState("edit");
  const history = useHistory();
  const { authUser } = useContext(UserContext);

  const loadData = () => {
    axios
      .get(`${endpoint}/config/claimaccuracycondition/all`)
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
    if (!(edit.condition && edit.query)) return;

    if (mode === "edit") {
      console.log(edit)
      axios
        .put(`${endpoint}/config/claimaccuracycondition/update/${edit.condition}`, edit)
        .then((res) => {
          message.success(res.data);
          handleReset();
          loadData();
        })
        .catch((err) => message.error(err.res.data));
    } else if (mode === "add") {
      axios
        .post(`${endpoint}/config/claimaccuracycondition/add`, edit)
        .then((res) => {
          message.success(res.data);
          handleReset();
          loadData();
        })
        .catch((err) => message.error(err.res.data));
    }
  };

  const handleReset = () => {
    setEdit({ condition: "", query: "", active :true });
    resetMode();
  };

  const resetMode = () => {
    setMode("edit");
  };

  const handleDelete = (condition) => {
    Modal.confirm({
      title: `Delete Mapping For '${condition}' ?`,
      okText: "Yes",
      cancelText: "No",
      content: "This action cannot be undone.",
      icon: <CloseCircleTwoTone />,
      centered: true,
      mask: true,
      maskClosable: true,
      onOk: () => {
        axios
          .delete(`${endpoint}/config/claimaccuracycondition/delete/${condition}`)
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
        <span>Instead of Claim ID mention as </span>
        <span className="syntax">{"'{claimid}'"}</span>
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
          <TextArea
            value={edit.condition}
            name="condition"
            placeholder="Condition Name"
            className="mapping_form_field"
            disabled={mode === "edit"}
            onChange={(e) => handleChange(e)}
            autoSize
          />
          
          <TextArea
            value={edit.query}
            name="query"
            placeholder="Validation Query"
            className="query"
            onChange={(e) => handleChange(e)}
            autoSize
          />
          <div className="mapping_form_field check">
            <input
              type="checkbox"
              checked={edit.active}
              id="active"
              name="active"
              onChange={(e) => handleCheckChange(e)}
            />
            <label htmlFor="active">Active?</label>
          </div>
          {/* <Checkbox checked={edit.valueOverridable} name="valueOverridable" className="mapping_form_field check" onChange={e=>handleCheckChange(e)} >Mockup Eligible?</Checkbox> */}
          <Button
            type="primary"
            htmlType="submit"
            className="mapping_form_button"
            disabled={!(edit.condition && edit.query)}
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
                      condition: d.condition,
                      query: d.query,
                      active: d.active,
                    })
                  }
                />
                <Button
                  type="link"
                  onClick={(e) => handleDelete(d.condition)}
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

export default ClaimAccuracyConditions;
