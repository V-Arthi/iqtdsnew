import React, { useContext } from "react";
import { Layout, Menu } from "antd";
import {
  RocketFilled,
  ContainerFilled,
  ClusterOutlined,
  AppstoreFilled,
  ToolFilled,
  ControlOutlined,
  DashboardOutlined,
  ReconciliationOutlined,
  ExperimentFilled,
  UploadOutlined,
} from "@ant-design/icons";
import { Link } from "react-router-dom";
import { UserContext } from "../Contexts/UserContext";
import "../styles/side-nav.css";

const { Sider } = Layout;
const { SubMenu } = Menu;
const config = require("../config.json");
const screen_access = config.screen_access;

function SideNav() {
  const { authUser } = useContext(UserContext);

  return authUser.authenticated ? (
    <Sider collapsible className="side_nav" defaultCollapsed>
      <Menu
        theme="dark"
        defaultSelectedKeys="execute"
        mode="inline"
        className="side_nav_menu"
      >
        <SubMenu
          key="Claims"
          icon={<AppstoreFilled />}
          title="Claims"
          className="side_nav_item"
        >
          {screen_access.execute.roles.indexOf(authUser.role) >= 0 && (
            <Menu.Item
              key="execute"
              icon={<RocketFilled />}
              className="side_nav_item"
            >
              <Link to={{ pathname: "/execute" }}>Execute</Link>
            </Menu.Item>
          )}
          {screen_access.design.roles.indexOf(authUser.role) >= 0 && (
            <Menu.Item
              key="design"
              icon={<ClusterOutlined />}
              className="side_nav_item"
            >
              <Link to={{ pathname: "/design" }}>Design</Link>
            </Menu.Item>
          )}
          {screen_access.jobs.roles.indexOf(authUser.role) >= 0 && (
            <Menu.Item
              key="jobs"
              icon={<ContainerFilled />}
              className="side_nav_item"
            >
              <Link to={{ pathname: "/jobs" }}>Jobs</Link>
            </Menu.Item>
          )}
          {screen_access.dashboard.roles.indexOf(authUser.role) >= 0 && (
            <Menu.Item key="number-view" icon={<DashboardOutlined />}>
              <Link to={{ pathname: "/dashboard" }}>Dashboard</Link>
            </Menu.Item>
          )}
          {screen_access.settings.roles.indexOf(authUser.role) >= 0 && (
            <Menu.Item key="837-mappings" icon={<ControlOutlined />}>
              <Link to={{ pathname: "/config/mapping" }}>Manage Mappings</Link>
            </Menu.Item>
          )}
          {screen_access.execute.roles.indexOf(authUser.role) >= 0 && (
            <Menu.Item
              key="validate"
              icon={<ReconciliationOutlined />}
              className="side_nav_item"
            >
              <Link to={{ pathname: "/validate/claims" }}>Validate</Link>
            </Menu.Item>
          )}
          {screen_access.execute.roles.indexOf(authUser.role) >= 0 && (
            <Menu.Item
              key="testcases"
              icon={<ExperimentFilled />}
              className="side_nav_item"
            >
              <Link to={{ pathname: "/testcases" }}>Testcases</Link>
            </Menu.Item>
          )}
          {screen_access.execute.roles.indexOf(authUser.role) >= 0 && (
            <Menu.Item
              key="bulkload"
              icon={<UploadOutlined/>}
              className="side_nav_item"
            >
              <Link to={{ pathname: "/bulkload" }}>Bulk Load</Link>
            </Menu.Item>
          )}
        </SubMenu>
        <SubMenu
          key="Tools"
          icon={<ToolFilled />}
          title="Tools"
          className="side_nav_item"
        >
          {screen_access.execute.roles.indexOf(authUser.role) >= 0 && (
            <Menu.Item
              key="claimaccuracyvalidator"
              icon={<ReconciliationOutlined />}
              className="side_nav_item"
            >
              <Link to={{ pathname: "/tools/claimaccuracyvalidator" }}>Claim Accuracy Validator</Link>
            </Menu.Item>
          )}
          {screen_access.execute.roles.indexOf(authUser.role) >= 0 && (
            <Menu.Item
              key="claimaccuracyconditions"
              icon={<ReconciliationOutlined />}
              className="side_nav_item"
            >
              <Link to={{ pathname: "/tools/claimaccuracyvalidator/conditions" }}>Claim Accuracy Validator Conditions</Link>
            </Menu.Item>
          )}
        </SubMenu>
      </Menu>
    </Sider>
  ) : (
    <></>
  );
}

export default SideNav;
