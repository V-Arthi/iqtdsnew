import React, { useEffect, useState, useContext } from "react";
import { Link, useHistory } from "react-router-dom";
import {
  Table,
  Button,
  Input,
  Space,
  Tooltip,
  Tag,
  message,
  Modal,
} from "antd";
import {
  CaretRightOutlined,
  SearchOutlined,
  EditOutlined,
  PlusOutlined,
  DeleteFilled,
  CloseCircleTwoTone,
} from "@ant-design/icons";
import axios from "axios";
import { v4 as uuid } from "uuid";
import { UserContext } from "../Contexts/UserContext";

import "../styles/testsgrid.css";
const config = require("../config.json");
const endpoint = process.env.REACT_APP_API_URL;
const environments = config.environments;

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
    title: "ID",
    dataIndex: "testId",
    key: "testId",
    ...getColumnSearchProps("testId"),
  },
  {
    title: "Condition Name",
    key: "testName",
    dataIndex: "testName",
    ...getColumnSearchProps("testName"),
  },
  {
    title: "Source",
    dataIndex: "claimInputType",
    key: "claimInputType",
    ...getColumnSearchProps("claimInputType"),
  },
  {
    title: "Type",
    dataIndex: "claimType",
    key: "claimType",
    ...getColumnSearchProps("claimType"),
  },
  {
    title: "Tags",
    dataIndex: "tags",
    key: "tags",
    ...getColumnSearchProps("tags"),
    render: (tags) =>
      tags &&
      tags.split(",").map((t) => {
        return (
          <Tag key={uuid()} color="cyan">
            {t.toUpperCase()}
          </Tag>
        );
      }),
  },
];

const filterColumns = [
  { title: "Field", dataIndex: "field", key: "field" },
  { title: "Operator", dataIndex: "operator", key: "operator" },
  { title: "Value", dataIndex: "value", key: "value" },
];

const envOptions = environments.map((env) => (
  <option key={uuid()} value={env}>
    {env}
  </option>
));

const loadData = () => {
  return axios.get(`${endpoint}/conditions`);
};

const wholeNumbersOnly = /^[0-9\b]{1,3}$/;

function TestsGrid(props) {
  const { authUser } = useContext(UserContext);

  const history = useHistory();

  const [data, setData] = useState([]);

  const [allEnv, setAllEnv] = useState(environments[0]);

  const [loading, setLoading] = useState(true);

  const [selected, setSelected] = useState({});

  const runMode = props.runMode;

  const handleItemEnvChange = (e, index) => {
    const { name, value } = e.target;
    //console.log('name-value',`${name}=${value} at index ${index}`)
    const data__clone = [...data];
    data__clone[index][name] = value;
    setData(data__clone);
    //console.log(data)
  };

  const handleRecordLimitChange = (e, index) => {
    const value = e.target.value;
    if (value === "" || wholeNumbersOnly.test(value)) {
      const data_clone = [...data];
      data_clone[index]["recordLength"] = value;
      setData(data_clone);
    }
  };

  const rowSelection = {
    onChange: (selectedRowKeys, selectedRows) => {
      const selectedIds = selectedRows.map(row => +row.testId);
      // Update row mode / reset to old data
      setData(data.map(d => {
        const changedData = {
          mode: selectedIds.includes(+d.testId) ? "e" : "r",
          ...(
            selectedIds.includes(+d.testId)
              ? {}
              : (selected[d.testId] || {})
          )
        };
        return {
          ...d,
          ...changedData
        };
      }));
      // Updated Old Selected Rows to New Selected Rows
      setSelected(selectedRows.reduce((acc, item) => {
        return {
          ...acc,
          [item.testId]: {
            test_env: item.test_env,
            recordLength: item.recordLength
          }
        };
      }, {}));
    },
  };

  const run = (selected) => {
    history.push({
      pathname: "/job/submit",
      state: { selected },
    });
  };

  const execute = () => {
    const selectedIds = Object.keys(selected).map(id => +id);
    const executable = data.reduce((acc, item) => {
      if (selectedIds.includes(+item.testId)) {
        acc.push({
          testId: item.testId,
          env: item.test_env,
          recordLength: item.recordLength
        });
      }
      return acc;
    }, []);
    run(executable);
  };

  useEffect(() => {
    loadData()
      .then((res) => {
          
        // console.log(res.data)
        setData(
          res.data.map((d) =>({ ...d,
            testName: `${d.brName}  ${d.testName}`, 
            test_env: allEnv, 
            recordLength: 5, 
            mode: "r" ,
          }))
        );
        setLoading(false);
      })
      .catch((err) => message.error(err.message, 60));
  }, [allEnv]);

  const hasSelected = Object.keys(selected).length > 0;

  const deleteThisCondition = (testID) => {
    Modal.confirm({
      title: `Delete Test Condition '${testID}' ?`,
      okText: "Yes",
      cancelText: "No",
      content:
        "This will delete associated executions and jobs.This action cannot be undone.",
      icon: <CloseCircleTwoTone />,
      centered: true,
      mask: true,
      maskClosable: true,
      onOk: () => {
        setLoading(true);
        axios
          .delete(`${endpoint}/condition/delete/${testID}`)
          .then((res) => {
            message.success(res.data);
            loadData()
              .then((response) => {
                setData(response.data.map((d) => ({ ...d, test_env: allEnv, mode: "r" })));
                setLoading(false);
              })
              .catch((error) => {
                console.error("err", error);
              });
          })
          .catch((err) =>
            message.error(JSON.stringify(err.response.data, null, 2))
          );
      },
      okButtonProps: { danger: true },
    });
  };

  return (
    <div
      className={
        runMode ? "container execution-container" : "container manage-container"
      }
    >
      {runMode ? (
        <div
          style={{ marginBottom: 16 }}
          className="execution-container__header"
        >
          <div className="execution-container__selection">
            <Button
              onClick={execute}
              disabled={!hasSelected}
              icon={<CaretRightOutlined />}
              type="primary"
            >
              Run
            </Button>
            <span style={{ marginLeft: 8 }}>
              {hasSelected ? `Selected ${Object.keys(selected).length} items` : ""}
            </span>
          </div>
          <div className="execution-container__enviroment">
            {/* <Button type="link" style={{marginRight:10}} >Set For All ?</Button> */}
            <label htmlFor="environment__select" style={{ marginRight: 10 }}>
              Environment
            </label>
            <select
              name="environment__select"
              value={allEnv}
              onChange={(e) => setAllEnv(e.target.value)}
            >
              {envOptions}
            </select>
          </div>
        </div>
      ) : (
        <div style={{ marginBottom: 16 }} className="manage-container__add">
          <Tooltip title="add">
            <Link
              type="button"
              className="ant-btn ant-btn-primary btn add_btn"
              to={{
                pathname: "/add",
                condition: {
                  testId: "",
                  testName: "",
                  claimInputType: "",
                  claimType: "",
                  providerType: "",
                  claimFilters: [],
                  memberFilters: [],
                  providerFilters: [],
                },
              }}
            >
              <PlusOutlined style={{ marginRight: 5 }} />
              Add New
            </Link>
          </Tooltip>
        </div>
      )}
      <Table
        loading={loading}
        bordered
        small
        expandedRowRender={(record) => (
          <div>
            {record.claimFilters.length > 0 && (
              <Table
                title={() => <Tag>Claim Filter</Tag>}
                key={`${record.testId}_claim`}
                columns={filterColumns}
                dataSource={record.claimFilters.map((filter) => ({
                  ...filter,
                  key: filter.id,
                }))}
                bordered
              />
            )}

            {record.memberFilters.length > 0 && (
              <Table
                title={() => <Tag>Member Filter</Tag>}
                key={`${record.testId}_member`}
                columns={filterColumns}
                dataSource={record.memberFilters.map((filter) => ({
                  ...filter,
                  key: filter.id,
                }))}
                bordered
              />
            )}

            {record.providerFilters.length > 0 && (
              <Table
                title={() => (
                  <Tag>
                    Provider Filter | Provider Type : {record.providerType}
                  </Tag>
                )}
                key={`${record.testId}_provider`}
                columns={filterColumns}
                dataSource={record.providerFilters.map((filter) => ({
                  ...filter,
                  key: filter.id,
                }))}
                bordered
              />
            )}
          </div>
        )}
        title={() => "Test Conditions"}
        rowSelection={
          runMode && {
            type: "checkbox",
            ...rowSelection,
          }
        }
        columns={
          runMode
            ? [
              ...columns,
              { title: "Env", dataIndex: "env", key: "env" },
              {
                title: "# Records",
                dataIndex: "recordLimit",
                key: "recordLimit",
              },
            ]
            : [...columns, { title: "", dataIndex: "action", key: "action" }]
        }
        dataSource={data.map((item, index) => {
          return runMode
            ? {
              ...item,
              key: item.testId,
              env: (
                item.mode === "e" ? (
                  <select
                    name="test_env"
                    value={item.test_env}
                    onChange={(e) => handleItemEnvChange(e, index)}
                  >
                    {environments.map((item, index) => {
                      return (
                        <option key={index} value={item}>
                          {item}
                        </option>
                      );
                    })}
                  </select>
                ) : item.test_env
              ),
              recordLimit: (
                item.mode === "e" ? (
                  <Input
                    className="record_limit_inp"
                    type="number"
                    name="recordLength"
                    value={item.recordLength}
                    onChange={(e) => handleRecordLimitChange(e, index)}
                  />
                ) : item.recordLength
              ),
            }
            : {
              ...item,
              key: item.testId,
              action: (
                <>
                  <Link to={{ pathname: `update/${item.testId}` }}>
                    <EditOutlined />
                  </Link>
                  {authUser.authenticated &&
                    ["administrator", "maintainer"].indexOf(authUser.role) >=
                    0 && (
                      <Button
                        style={{ marginLeft: 10 }}
                        onClick={(e) => deleteThisCondition(item.testId)}
                        type="link"
                        icon={<DeleteFilled />}
                        danger
                      />
                    )}
                </>
              ),
            };
        })}
      />
    </div>
  );
}

export default TestsGrid;
