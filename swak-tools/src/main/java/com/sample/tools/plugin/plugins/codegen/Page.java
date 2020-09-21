package com.sample.tools.plugin.plugins.codegen;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.sample.tools.config.Settings;
import com.sample.tools.plugin.plugins.codegen.gen.DatabaseOperater;
import com.sample.tools.plugin.plugins.codegen.gen.GenUtils;
import com.sample.tools.plugin.plugins.codegen.gen.Scheme;
import com.sample.tools.plugin.plugins.codegen.gen.Table;
import com.swak.fx.support.Display;
import com.swak.fx.support.FXMLView;
import com.swak.fx.support.Notifys;
import com.swak.fx.support.Window;
import com.swak.reactivex.threads.Contexts;
import com.swak.reactivex.threads.WorkerContext;
import com.swak.utils.Lists;
import com.swak.utils.StringUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 * 页面
 * 
 * @author lifeng
 * @date 2020年6月2日 下午3:57:40
 */
/**
 * 设置页面
 * 
 * @author lifeng
 */
@FXMLView(title = "开发工具.代码生成", value = "Page.fxml", css = { "Page.css" }, stageStyle = "TRANSPARENT")
public class Page extends Window {

	@FXML
	private ChoiceBox<String> dbType;
	@FXML
	private TextField dbIp;
	@FXML
	private TextField dbPort;
	@FXML
	private ChoiceBox<String> dbSel;
	@FXML
	private ChoiceBox<String> tableSel;
	@FXML
	private TextField userName;
	@FXML
	private TextField passWord;
	@FXML
	private TextField packageName;
	@FXML
	private TextField entityName;
	@FXML
	private Button buildBtn;
	@FXML
	private Button refreshBtn;
	private List<String> dbs = Lists.newArrayList("MsSql", "MySql");

	// 数据操作
	private DatabaseOperater databaseOperater;
	private WorkerContext context;

	@FXML
	public void initialize() {
		this.context = Contexts.createWorkerContext("CodeGen", 1, true, 60, TimeUnit.SECONDS);
		dbType.getItems().addAll(dbs);
		String db = Settings.me().getConfig().string("codegen.db");
		String udb = Settings.me().getConfig().string("codegen.udb");
		String dbIp = Settings.me().getConfig().string("codegen.ip");
		String dbPort = Settings.me().getConfig().string("codegen.port");
		String userName = Settings.me().getConfig().string("codegen.un");
		String passWord = Settings.me().getConfig().string("codegen.pw");
		String packageName = Settings.me().getConfig().string("codegen.pkn");
		if (StringUtils.isNotBlank(db)) {
			int i = 0;
			for (String _db : dbs) {
				if (_db.equals(db)) {
					dbType.getSelectionModel().clearAndSelect(i);
					break;
				}
				i++;
			}
		}
		if (StringUtils.isNotBlank(udb)) {
			dbSel.getItems().add(udb);
		}
		if (StringUtils.isNotBlank(dbIp)) {
			this.dbIp.setText(dbIp);
		}
		if (StringUtils.isNotBlank(dbPort)) {
			this.dbPort.setText(dbPort);
		}
		if (StringUtils.isNotBlank(userName)) {
			this.userName.setText(userName);
		}
		if (StringUtils.isNotBlank(passWord)) {
			this.passWord.setText(passWord);
		}
		if (StringUtils.isNotBlank(packageName)) {
			this.packageName.setText(packageName);
		}
		this.dbType.getSelectionModel().selectedItemProperty().addListener((ob, o, n) -> {
			Settings.me().getConfig().string("codegen.db", n);
			if (o == null || n == null || !o.equals(n)) {
				this.dbIp.setText(null);
				this.dbPort.setText(null);
				this.userName.setText(null);
				this.passWord.setText(null);
				this.dbSel.getItems().clear();
				this.tableSel.getItems().clear();
				this.tryOpenDb();
			}
		});
		this.dbIp.textProperty().addListener((ob, o, n) -> {
			Settings.me().getConfig().string("codegen.ip", n);
			this.tryOpenDb();
		});
		this.dbPort.textProperty().addListener((ob, o, n) -> {
			Settings.me().getConfig().string("codegen.port", n);
			this.tryOpenDb();
		});
		this.userName.textProperty().addListener((ob, o, n) -> {
			Settings.me().getConfig().string("codegen.un", n);
			this.tryOpenDb();
		});
		this.passWord.textProperty().addListener((ob, o, n) -> {
			Settings.me().getConfig().string("codegen.pw", n);
			this.tryOpenDb();
		});
		this.dbSel.getSelectionModel().selectedItemProperty().addListener((ob, o, n) -> {
			Settings.me().getConfig().string("codegen.udb", n);
			this.loadTables();
		});
		this.tableSel.getSelectionModel().selectedItemProperty().addListener((ob, o, n) -> {
			String entityName = StringUtils.contains(n, "_") ? StringUtils.substringAfter(n, "_") : n;
			entityName = StringUtils.upperCaseFirstOne(StringUtils.convertColumn2Property(entityName));
			this.entityName.setText(entityName);
		});
		this.packageName.textProperty().addListener((ob, o, n) -> {
			Settings.me().getConfig().string("codegen.pkn", n);
		});
		this.buildBtn.setOnAction((actionEvent) -> {
			this.genCode();
		});
		this.refreshBtn.setOnAction((actionEvent) -> {
			this.tryOpenDb();
		});

		this.tryOpenDb();
		super.initialize();
	}

	/**
	 * 尝试打开数据库
	 */
	private void tryOpenDb() {
		this.context.execute(() -> {
			String dbType = this.dbType.getSelectionModel().getSelectedItem();
			String ip = this.dbIp.getText();
			String port = this.dbPort.getText();
			String un = this.userName.getText();
			String pw = this.passWord.getText();

			if (!(StringUtils.isNotBlank(ip) && StringUtils.isNotBlank(port) && StringUtils.isNotBlank(un)
					&& StringUtils.isNotBlank(pw))) {
				return;
			}

			if (databaseOperater != null) {
				databaseOperater.close();
			}
			databaseOperater = DatabaseOperater.build(dbType, ip, port, un, pw);
			databaseOperater.open();
			if (databaseOperater.isActive()) {
				List<String> dbs = databaseOperater.getDbs();
				this.showDbs(dbs);
			}
			Settings.me().storeConfig();
		});
	}

	private void showDbs(List<String> dbs) {
		Display.runUI(() -> {
			String udb = this.dbSel.getSelectionModel().getSelectedItem();
			this.dbSel.getItems().addAll(dbs);
			if (StringUtils.isNotBlank(udb)) {
				this.dbSel.getSelectionModel().select(udb);
				this.loadTables();
			}
		});
	}

	private void loadTables() {
		String udb = this.dbSel.getSelectionModel().getSelectedItem();
		if (StringUtils.isNotBlank(udb)) {
			this.context.execute(() -> {
				if (databaseOperater.isActive()) {
					List<String> dbs = databaseOperater.getTables(udb);
					this.showTables(dbs);
				}
			});
		}
	}

	private void showTables(List<String> dbs) {
		Display.runUI(() -> {
			String udb = this.tableSel.getSelectionModel().getSelectedItem();
			this.tableSel.getItems().addAll(dbs);
			if (StringUtils.isNotBlank(udb)) {
				this.tableSel.getSelectionModel().select(udb);
			}
		});
	}

	private void genCode() {
		String udb = this.dbSel.getSelectionModel().getSelectedItem();
		String utable = this.tableSel.getSelectionModel().getSelectedItem();
		String packageName = this.packageName.getText();
		String entityName = this.entityName.getText();
		if (!(StringUtils.isNotBlank(udb) && StringUtils.isNotBlank(utable) && StringUtils.isNotBlank(packageName)
				&& StringUtils.isNotBlank(entityName))) {
			Notifys.error("操作", "请填写正确的信息！");
			return;
		}
		this.context.execute(() -> {
			if (databaseOperater.isActive()) {
				Table table = databaseOperater.getTable(udb, utable);
				Scheme scheme = new Scheme();
				scheme.setTable(table);
				scheme.setPackageName(packageName);
				scheme.setFunctionName(entityName);
				GenUtils.genCode(scheme);
				Notifys.info("代码创建成功", "生成的代码已经保存在桌面！");
			}
		});
	}

	/**
	 * 等待页面关闭
	 */
	public CompletableFuture<Void> waitClose() {
		if (!closeFuture.isDone()) {
			closeFuture.complete(null);
		}
		return initFuture.thenCompose((v) -> {
			return closeFuture;
		});
	}

	/**
	 * 关闭
	 */
	@Override
	public void onClose(MouseEvent evt) {
		this.waitClose().thenAcceptAsync((v) -> {
			Settings.me().storeConfig();
		}).thenAccept((v) -> {
			Display.runUI(() -> {
				super.onClose(evt);
			});
		});
	}
}
