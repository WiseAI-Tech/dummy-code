<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<% String isAllowPWA = request.getParameter("isAllowPWA"); %>
<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="webparts_include/import.jsp" />
<style>
/*Custom Scroll To Top*/
.scroll-to-initial {
    position: fixed;
    right: 1rem;
    bottom: 1rem;
    display: none;
    width: 2.75rem;
    height: 2.75rem;
    text-align: center;
    color: #fff;
    background: rgba(90, 92, 105, .5);
    line-height: 46px
}

.scroll-to-initial:focus,
.scroll-to-initial:hover {
    color: #fff
}

.scroll-to-initial:hover {
    background: #5a5c69
}

.scroll-to-initial i {
    font-weight: 800
}

/*Overwrite Default Text Color*/
body, table, table.table {
	color: #000000;
}

.cursor-pointer {
	cursor: pointer;
}
a.nav-link.active {
	background-color: 
}
.side-bar-bg {
	background:
		url('${pageContext.request.contextPath}/admin/image/background/drawer_background.jpg')
		no-repeat center center fixed;
	background-size: cover;
}
table.dataTable thead {
	background: #FFFFFF;
}
table.datatable-fixed-header-table {
	margin: 0 !important;
}

/*Remove Input Spinner*/
/* Chrome, Safari, Edge, Opera */
input.no-input-spinner[type=number]::-webkit-outer-spin-button,
input.no-input-spinner[type=number]::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

/* Firefox */
input.no-input-spinner[type=number] {
  -moz-appearance:textfield;
}
</style>
<script>
var localStoragePrefix = "4f_my_";

function confirmLogout() {
	var dialogOption = {};
	dialogOption.isCloseButton = false;
	dialogOption.isCloseOnBackground = false;
	dialogOption.title = "<spring:message code='gt.page_logout.confirm_logout.title' />";
	dialogOption.description = "<spring:message code='gt.page_logout.confirm_logout.description' />";
	dialogOption.button1 = {};
	dialogOption.button1.isShow = true;
	dialogOption.button1.text = "<i class='fas fa-times'></i>&nbsp;<spring:message code='gt.button.no' />";
	dialogOption.button1.importantFlag = false;
	dialogOption.button1.fn = function() {
		hideDialog();
	};
	dialogOption.button2 = {};
	dialogOption.button2.isShow = true;
	dialogOption.button2.text = "<i class='fas fa-check'></i>&nbsp;<spring:message code='gt.button.yes' />";
	dialogOption.button2.importantFlag = true;
	dialogOption.button2.fn = function() {
		logout();
		hideDialog();
	};
	showDialog(dialogOption);
}

function logout() {
	showLoading("<spring:message code='gt.loading.logging_out' />...");
	setTimeout(function(){ 
		location.href = "${pageContext.request.contextPath}/admin/logout";
	}, 500);
}

function changeTitle(titleMessage) {
	$("b#headerTitle").html(titleMessage);
}

function showLoading(loadingMessage) {
	$("#loadingModal #loadingMessage").html(loadingMessage);
	$("#loadingModal").modal('show');
}

function changeLoadingMessage(loadingMessage) {
	$("#loadingModal #loadingMessage").html(loadingMessage);
}

function hideLoading() {
	$("#loadingModal").modal('hide');
}

function showMessage(message) {
	var dialogOption = {};
	dialogOption.isCloseButton = false;
	dialogOption.isCloseOnBackground = false;
	dialogOption.title = message;
	dialogOption.description = "";
	dialogOption.button1 = {};
	dialogOption.button1.isShow = true;
	dialogOption.button1.text = "<i class='fas fa-times'></i>&nbsp;<spring:message code='gt.button.ok' />";
	dialogOption.button1.importantFlag = false;
	dialogOption.button1.fn = function() {
		hideDialog();
	};
	showDialog(dialogOption);
}

function showDialog(dialogOption) {
	//$("#dialogModal").modal('dispose');
	
	if (dialogOption.isCloseButton) {
		$("#dialogModal #dialogModalCloseButton").show();
	} else {
		$("#dialogModal #dialogModalCloseButton").hide();
	}

	$("#dialogModal #dialogModalTitle").html(dialogOption.title);
	if (!dialogOption.description || dialogOption.description == null || dialogOption.description == "") {
		$("#dialogModal #dialogModalDescription").hide();
	} else {
		$("#dialogModal #dialogModalDescription").show();
	}
	$("#dialogModal #dialogModalDescription").html(dialogOption.description);

	if (!dialogOption.button1 || dialogOption.button1 == null || !dialogOption.button1.isShow) {
		$("#dialogModal #dialogModalButton1").hide();
	} else {
		$("#dialogModal #dialogModalButton1").show();

		if (dialogOption.button1 && dialogOption.button1.importantFlag) {
			$("#dialogModal #dialogModalButton1").removeClass("btn-primary btn-secondary");
			$("#dialogModal #dialogModalButton1").addClass("btn-primary");
		} else {
			$("#dialogModal #dialogModalButton1").removeClass("btn-primary btn-secondary");
			$("#dialogModal #dialogModalButton1").addClass("btn-secondary");
		}
		$("#dialogModal #dialogModalButton1").html(dialogOption.button1.text);
		$("#dialogModal #dialogModalButton1").off("click");
		$("#dialogModal #dialogModalButton1").on("click", dialogOption.button1.fn);
	}
	
	if (!dialogOption.button2 || dialogOption.button2 == null || !dialogOption.button2.isShow) {
		$("#dialogModal #dialogModalButton2").hide();
	} else {
		$("#dialogModal #dialogModalButton2").show();

		if (dialogOption.button2 && dialogOption.button2.importantFlag) {
			$("#dialogModal #dialogModalButton2").removeClass("btn-primary btn-secondary");
			$("#dialogModal #dialogModalButton2").addClass("btn-primary");
		} else {
			$("#dialogModal #dialogModalButton2").removeClass("btn-primary btn-secondary");
			$("#dialogModal #dialogModalButton2").addClass("btn-secondary");
		}
		$("#dialogModal #dialogModalButton2").html(dialogOption.button2.text);
		$("#dialogModal #dialogModalButton2").off("click");
		$("#dialogModal #dialogModalButton2").on("click", dialogOption.button2.fn);
	}

	$('#dialogModal').data('bs.modal',null);
	if (dialogOption.isCloseOnBackground) {
		$("#dialogModal").modal({show: true, backdrop: true, keyboard: true});
	} else {
		$("#dialogModal").modal({show: true, backdrop: 'static', keyboard: false});
	}
}

function hideDialog() {
	$("#dialogModal").modal('hide');
}

function showRefreshPage() {
	$("body").addClass("overflow-hidden");
	
	$("#refreshPageModal").height($("#content-container").outerHeight(true));
	$("#refreshPageModal").width($("#content-container").outerWidth(true));
	$("#refreshPageModal").show();
}

function hideRefreshPage() {
	$("#refreshPageModal").hide();
}

function refreshPage() {
	showLoading("<spring:message code='gt.loading.loading' />...");
	location.reload();
}

function showLoadError(errMsg) {
	var dialogOption = {};
	dialogOption.isCloseButton = false;
	dialogOption.isCloseOnBackground = false;
	if (errMsg && errMsg.length > 0) {
		dialogOption.title = errMsg;
	} else {
		dialogOption.title = "<spring:message code='gt.error.general.load_error' />";
	}
	dialogOption.button1 = {};
	dialogOption.button1.isShow = true;
	dialogOption.button1.text = "<spring:message code='gt.button.ok' />";
	dialogOption.button1.importantFlag = true;
	dialogOption.button1.fn = function() {
		hideDialog();
	};
	showDialog(dialogOption);

	hideLoading();

	showRefreshPage();
}

function showSessionExpired() {
	var dialogOption = {};
	dialogOption.isCloseButton = false;
	dialogOption.isCloseOnBackground = false;
	dialogOption.title = "<spring:message code='gt.error.general.session_timeout' />";
	dialogOption.button1 = {};
	dialogOption.button1.isShow = true;
	dialogOption.button1.text = "<spring:message code='gt.button.ok' />";
	dialogOption.button1.importantFlag = true;
	dialogOption.button1.fn = function() {
		location.href = "${pageContext.request.contextPath}/admin/";
		hideDialog();

		showLoading();
	};
	showDialog(dialogOption);
}

function changeLocale(locale) {
	location.replace(location.href.split('#!')[0].split('?')[0] + "?lang=" + locale + "#!" + location.href.split('#!')[1]);
}

//Customer Handler
$(document).on("click", ".collapse-item", function() {
	$(this).parents("#accordionSidebar div.collapse").collapse('hide');
});
$(document).click(function(evt) {
	if (!$(evt.target).parents("div.collapse").length) {
		$("#accordionSidebar div.collapse").collapse('hide');
	}
});

//Handler on Multi-Bootstrap Modal Dialog
$(document).ready(function() {
	$(document).on("hidden.bs.modal", '.modal', function (e) {
		if ($('.modal:visible').length) {
		    $('body').addClass('modal-open');
		} else {
			$('body').removeClass('modal-open');
			$('.modal-backdrop').remove();
		}
	});
});
</script>
</head>
<body id="page-top" ng-app="OmniU" class="sidebar-toggled">
	<!-- Page Wrapper -->
	<div id="wrapper">

		<!-- Sidebar -->
		<jsp:include page="/WEB-INF/admin/webparts_include/sidebar.jsp" />
		<!-- End of Sidebar -->

		<!-- Content Wrapper -->
		<div id="content-wrapper" class="d-flex flex-column"
			style="min-height: 100vh;">

			<!-- Main Content -->
			<div id="content"
				class="flex-fill d-flex flex-column overflow-hidden">

				<!-- Topbar -->
				<jsp:include page="/WEB-INF/admin/webparts_include/topbar.jsp" />
				<!-- End of Topbar -->

				<!-- Begin Page Content -->
				<div id="content-container"
					class="position-relative d-flex flex-column flex-fill p-0"
					style="overflow-x: hidden; overflow-y: auto;">
					<div ng-view class="d-flex flex-column flex-fill pl-2 pr-2"></div>
					<!-- Refresh Page Modal -->
					<div id="refreshPageModal" class="position-fixed"
						style="background-color: #0007; display: none; z-index: 1;">
						<div class="h-100 d-flex flex-column justify-content-center">
							<div class="d-flex flex-row justify-content-center">
								<button class="btn btn-primary" onclick="refreshPage()">Refresh
									Page</button>
							</div>
						</div>
					</div>
				</div>
				<!-- End Page Content -->
			</div>
			<!-- End of Main Content -->

			<!-- Footer -->
			<jsp:include page="/WEB-INF/admin/webparts_include/footbar.jsp" />
			<!-- End of Footer -->

		</div>
		<!-- End of Content Wrapper -->

	</div>
	<!-- End of Page Wrapper -->

	<!-- Scroll to Top Button-->
	<a class="scroll-to-initial rounded" href="javascript:void(0)"> <i
		class="fas fa-angle-up"></i>
	</a>
	
	<!-- Export Modal -->
	<div class="modal fade" id="exportModal" tabindex="-1" role="dialog"
		aria-labelledby="exportModal" aria-hidden="true">
		<div class="modal-dialog modal-xl modal-dialog-centered" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title"><spring:message code='gt.datatable.export' />&nbsp;(<span id="exportModalExportType" class="text-uppercase"></span>)</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<div class="row d-flex flex-row justify-content-center">
						<div class="col-12 col-sm-9 col-md-9 input-group mb-3">
							<div class="input-group-prepend">
								<span class="input-group-text"><spring:message code='gt.datatable.export_file_name' /></span>
							</div>
							<input type="text" class="form-control" id="exportModalFileName"
								placeholder="">
						</div>
					</div>
					<div class="row d-flex flex-row justify-content-center">
						<div class="col-3">
							<div><spring:message code='gt.datatable.export_available' />:</div>
							<select multiple class="form-control" size="10"
								id="export_AvailableSel">
							</select>
						</div>
						<div class="col-auto d-flex flex-column justify-content-center">
							<div class="d-flex flex-column">
								<button type="button" class="btn btn-sm btn-primary mb-1" onclick="moveExportToAvailable(true)">&lt;&lt;</button>
								<button type="button" class="btn btn-sm btn-primary mb-1" onclick="moveExportToAvailable(false)">&lt;</button>
								<button type="button" class="btn btn-sm btn-primary mb-1" onclick="moveAvailableToExport(false)">&gt;</button>
								<button type="button" class="btn btn-sm btn-primary mb-1" onclick="moveAvailableToExport(true)">&gt;&gt;</button>
							</div>
						</div>
						<div class="col-3">
							<div><spring:message code='gt.datatable.export' />:</div>
							<select multiple class="form-control" size="10"
								id="export_ExportSel">
							</select>
							<div class="d-flex flex-row justify-content-center">
								<button type="button" class="btn btn-sm btn-primary mr-1" onclick="reorderExportUp()"><i class="fas fa-angle-up"></i></button>
								<button type="button" class="btn btn-sm btn-primary mr-1" onclick="reorderExportDown()"><i class="fas fa-angle-down"></i></button>
							</div>
						</div>
						<div class="col-auto d-flex flex-column justify-content-center">
							<button type="button" class="btn btn-sm btn-primary mb-1" onclick="moveOrderToExport(true)">&lt;&lt;</button>
							<button type="button" class="btn btn-sm btn-primary mb-1" onclick="moveOrderToExport(false)">&lt;</button>
							<button type="button" class="btn btn-sm btn-primary mb-1" onclick="moveExportToOrder(false)">&gt;</button>
							<button type="button" class="btn btn-sm btn-primary mb-1" onclick="moveExportToOrder(true)">&gt;&gt;</button>
						</div>
						<div class="col-3">
							<div>Sort:</div>
							<select multiple class="form-control" size="10"
								id="export_OrderSel">
							</select>
							<div class="d-flex flex-row justify-content-center">
								<button type="button" class="btn btn-sm btn-primary mr-1" onclick="reorderOrderUp()"><i class="fas fa-angle-up"></i></button>
								<button type="button" class="btn btn-sm btn-primary mr-1" onclick="reorderOrderDown()"><i class="fas fa-angle-down"></i></button>
								<button type="button" class="btn btn-sm btn-primary mr-1" onclick="changeExportOrder('asc')"><i class="fas fa-arrow-down"></i>&nbsp;<spring:message code='gt.datatable.export_asc' /></button>
								<button type="button" class="btn btn-sm btn-primary mr-1" onclick="changeExportOrder('desc')"><i class="fas fa-arrow-up"></i>&nbsp;<spring:message code='gt.datatable.export_desc' /></button>
							</div>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary" onclick="tryExportData()">Export</button>
					<button type="button" class="btn btn-primary" data-dismiss="modal">Cancel</button>
				</div>
			</div>
		</div>
	</div>

	<!-- Dialog Modal -->
	<div class="modal fade" id="dialogModal" tabindex="-1" role="dialog"
		aria-labelledby="dialogModal" aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="dialogModalTitle"></h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close" id="dialogModalCloseButton">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body" id="dialogModalDescription">...</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						id="dialogModalButton1"></button>
					<button type="button" class="btn btn-primary"
						id="dialogModalButton2"></button>
				</div>
			</div>
		</div>
	</div>

	<!-- Loading Modal -->
	<div id="loadingModal" class="modal" tabindex="-1" role="dialog"
		data-backdrop="static">
		<div class="modal-dialog modal-dialog-centered" role="document">
			<div class="modal-content">
				<div class="modal-body">
					<div class="d-flex justify-content-center">
						<div class="spinner-border text-warning m-4" role="status">
							<span class="sr-only"><spring:message
									code="gt.loading.loading" />...</span>
						</div>
					</div>
					<div class="d-flex justify-content-center text-center">
						<span><b id="loadingMessage"></b></span>
					</div>
				</div>
			</div>
		</div>
	</div>

	<script
		src="${pageContext.request.contextPath}/plugins/sb-admin-2/js/sb-admin-2.min.js"></script>
	<script>
		$(window).on("scroll",function() {
			$(this).scrollTop()>50?$(".scroll-to-initial").fadeIn():$(".scroll-to-initial").fadeOut();
		});
		$(document).off("click", "a.scroll-to-initial");
		$(document).on("click", "a.scroll-to-initial", function(event) {
			$('html,body').animate({ scrollTop: 0 }, 'medium');
		})
	</script>
</body>
<script>
	function initializeColumnVisibilitySetting(settings) {
		var tableID = settings.sTableId;
		var columnsData = settings.aoColumns;
	
		var htmlData = "<div class='dropdown'>";
		htmlData += "<button class='btn btn-sm btn-primary dropdown-toggle' type='button' id='columnDropDown' data-toggle='dropdown' aria-haspopup='true' aria-expanded='false'><spring:message code='gt.datatable.display_columns' /></button>";
		htmlData += "<div class='dropdown-menu' aria-labelledby='columnDropDown' style='height: auto; max-height: 200px; overflow-x: hidden;'>";
	    for (var x = 0; x < columnsData.length; x++) {
		    var columnData = columnsData[x];
		    if (!columnData.sClass.includes("dt-no-visible-option")) {
		    	htmlData += "<div class='dropdown-item form-check-inline pl-2 pr-2' onclick='event.stopPropagation();'>";
				htmlData += "<label class='form-check-label w-100'>";
				if (columnData.bVisible) {
					htmlData += "<input type='checkbox' class='form-check-input toggle-visibility' value='' data-column='"+columnData.idx+"' checked>" + columnData.sTitle;
				} else {
					htmlData += "<input type='checkbox' class='form-check-input toggle-visibility' value='' data-column='"+columnData.idx+"'>" + columnData.sTitle;
				}
				htmlData += "</label>";
				htmlData += "</div>";
			}
		}
	    htmlData += "</div></div>";
	
	    if ($("#" + tableID + "_wrapper").find("div.row.dt-utility-row").length) {
	        if ($("#" + tableID + "_wrapper").find("div.row.dt-utility-row").first().find("div.dt-visibility-div").length) {
	        	$("#" + tableID + "_wrapper").find("div.row.dt-utility-row").first().find("div.dt-visibility-div").first().html(htmlData);
	        } else {
	        	$("#" + tableID + "_wrapper").find("div.row.dt-utility-row").first().append("<div class='flex-fill'></div><div class='col-auto dt-visibility-div'>"+htmlData+"</div>");
	        }
	    } else {
	    	$("#" + tableID + "_wrapper").children("div.row").first().after("<div class='row dt-utility-row'><div class='col-auto dt-visibility-div'>"+htmlData+"</div>");
	    }
		
		$("#" + tableID + "_wrapper " + "input.toggle-visibility").on('click', function (e) {
	        var column = $("#" + tableID).DataTable().column($(this).attr('data-column'));
	        column.visible(this.checked);
	    });
	}
	
	var maxSettingsIndex = 20;
	var settingsIndex = 0;
	var settingsCache = [];
	
	function initializeExportSetting(settings, fileName) {
		if (settingsIndex >= maxSettingsIndex) {
			settingsIndex = 0;
		}
	
		settingsCache[settingsIndex] = settings;
		settingsCache[settingsIndex].dtExportFileName = fileName;
		
		var tableID = settings.sTableId;
		var columnsData = settings.aoColumns;
	
		var htmlData = "<div class='dropdown'>";
		htmlData += "<button class='btn btn-sm btn-primary dropdown-toggle' type='button' id='columnDropDown' data-toggle='dropdown' aria-haspopup='true' aria-expanded='false'><spring:message code='gt.datatable.export' /></button>";
		htmlData += "<div class='dropdown-menu' aria-labelledby='columnDropDown' style='height: auto; max-height: 200px; overflow-x: hidden;'>";
		htmlData += "<div class='dropdown-item pl-2 pr-2' onclick='showExportDialog("+settingsIndex+",\"excel\")'>Excel</div>";
		/* Hide PDF First */
		//htmlData += "<div class='dropdown-item pl-2 pr-2' onclick='showExportDialog("+settingsIndex+",\"pdf\")'>PDF</div>";
	    htmlData += "</div></div>";
	
	    if ($("#" + tableID + "_wrapper").find("div.row.dt-utility-row").length) {
	        if ($("#" + tableID + "_wrapper").find("div.row.dt-utility-row").first().find("div.dt-export-div").length) {
	        	$("#" + tableID + "_wrapper").find("div.row.dt-utility-row").first().find("div.dt-export-div").first().html(htmlData);
	        } else {
	        	$("#" + tableID + "_wrapper").find("div.row.dt-utility-row").first().append("</div><div class='col-auto dt-export-div' style='margin-left: auto; order: 2;'>"+htmlData+"</div>");
	        }
	    } else {
	    	$("#" + tableID + "_wrapper").children("div.row").first().after("<div class='row dt-utility-row'><div class='col-auto dt-export-div' style='margin-left: auto; order: 2;'>"+htmlData+"</div>");
	    }
		
		$("#" + tableID + "_wrapper " + "input.toggle-visibility").on('click', function (e) {
	        var column = $("#" + tableID).DataTable().column($(this).attr('data-column'));
	        column.visible(this.checked);
	    });
	
		return settingsIndex++;
	}
	
	function changeDefaultFileName(settingIndex, fileName) {
		settingsCache[settingIndex].dtExportFileName = fileName;
	}
	
	var availableColumnList = [];
	var exportColumnList = [];
	var orderColumnList = [];
	var currentExportType = null;
	var currentSettingIndex = null;
	var exportPostingURL = null;
	var exportPostingType = null;
	var exportPostingData = null;
	var exportedFileName = null;
	
	function showExportDialog(settingsIndex, exportType) {
		var datatableSettings = $.extend(true, {}, settingsCache[settingsIndex]);
		
		currentSettingIndex = settingsIndex;
		currentExportType = exportType;
		currentFileName = datatableSettings.dtExportFileName;
		if (currentFileName && currentFileName != null && currentFileName != "") {
	    	fileName = currentFileName;
	    } else {
	    	fileName = "exportedFile";
	    }
		exportPostingURL = datatableSettings.ajax.url;
		exportPostingType = datatableSettings.ajax.type;
		exportPostingData = datatableSettings.ajax.data;
		if (exportPostingData == null) {
			exportPostingData = {};
		}
		exportPostingData.isExport = true;
		exportPostingData.exportType = exportType;
		exportPostingData.exportGlobalSearch = datatableSettings.oPreviousSearch.sSearch;
		
		availableColumnList = [];
		exportColumnList = [];
		orderColumnList = [];

		if (localStorage.getItem(localStoragePrefix+"export.table.data." + settingsCache[settingsIndex].nTable.id) != null) {
			var tableSettingStorage = JSON.parse(localStorage.getItem(localStoragePrefix+"export.table.data." + settingsCache[settingsIndex].nTable.id));

			availableColumnList = tableSettingStorage.availableColumnList;
			exportColumnList = tableSettingStorage.exportColumnList;
			orderColumnList = tableSettingStorage.orderColumnList;
		} else {
			var columnsData = datatableSettings.aoColumns;
			var columnsSearchData = datatableSettings.aoPreSearchCols;
			for (var x = 0; x < columnsData.length; x++) {
			    var columnData = columnsData[x];
			    var columnSearchData = columnsSearchData[x];
		
			    if (columnData.mData == null || columnData.mData == "") {
				    continue;
				}
			    
			    var availableColumnData = {};
		    	availableColumnData.name = columnData.title;
		    	availableColumnData.mData = columnData.mData;
		    	availableColumnData.bSearchable = columnData.bSearchable;
		    	availableColumnData.bSortable = columnData.bSortable;
		    	availableColumnData.sSearch = columnSearchData.sSearch;
		    	availableColumnData.bRegex = columnSearchData.bRegex;
		    	availableColumnData.isExportVisible = !columnData.sClass.includes("dt-no-export-option");
		
		    	availableColumnList.push(availableColumnData);
			}
		}
	
		refreshExportDialog();
		$("#exportModal #exportModalExportType").html(exportType);
		$("#exportModal #exportModalFileName").val(currentFileName);
		$("#exportModal").modal('show');
	}
	
	function tryExportData() {
		currentFileName = $("#exportModal #exportModalFileName").val();
	
		if (currentFileName == null || currentFileName == "") {
			var dialogOption = {};
			dialogOption.isCloseButton = false;
			dialogOption.isCloseOnBackground = false;
			dialogOption.title = "File Name Cannot Be Empty";
			dialogOption.button1 = {};
			dialogOption.button1.isShow = true;
			dialogOption.button1.text = "<spring:message code='gt.button.ok' />";
			dialogOption.button1.importantFlag = true;
			dialogOption.button1.fn = function() {
				hideDialog();
			};
			showDialog(dialogOption);
		} else if (exportColumnList.length == 0) {
			var dialogOption = {};
			dialogOption.isCloseButton = false;
			dialogOption.isCloseOnBackground = false;
			dialogOption.title = "You Must Choose At Least One Column To Export";
			dialogOption.button1 = {};
			dialogOption.button1.isShow = true;
			dialogOption.button1.text = "<spring:message code='gt.button.ok' />";
			dialogOption.button1.importantFlag = true;
			dialogOption.button1.fn = function() {
				hideDialog();
			};
			showDialog(dialogOption);
		} else if (orderColumnList.length == 0) {
			var dialogOption = {};
			dialogOption.isCloseButton = false;
			dialogOption.isCloseOnBackground = false;
			dialogOption.title = "You Must Choose At Least One Column To Order";
			dialogOption.button1 = {};
			dialogOption.button1.isShow = true;
			dialogOption.button1.text = "<spring:message code='gt.button.ok' />";
			dialogOption.button1.importantFlag = true;
			dialogOption.button1.fn = function() {
				hideDialog();
			};
			showDialog(dialogOption);
		} else {
			var tableSettingStorage = {};
			tableSettingStorage.availableColumnList = availableColumnList;
			tableSettingStorage.exportColumnList = exportColumnList;
			tableSettingStorage.orderColumnList = orderColumnList;
			localStorage.setItem(localStoragePrefix+"export.table.data." + settingsCache[currentSettingIndex].nTable.id, JSON.stringify(tableSettingStorage));
			
			for (var x = 0; x < exportColumnList.length; x++) {
				exportPostingData["columns["+x+"][isExportVisible]"] = exportColumnList[x].isExportVisible;
				exportPostingData["columns["+x+"][data]"] = exportColumnList[x].mData;
				exportPostingData["columns["+x+"][name]"] = exportColumnList[x].name;
				exportPostingData["columns["+x+"][searchable]"] = exportColumnList[x].bSearchable;
				exportPostingData["columns["+x+"][orderable]"] = exportColumnList[x].bSortable;
				exportPostingData["columns["+x+"][search][value]"] = exportColumnList[x].sSearch;
				exportPostingData["columns["+x+"][search][regex]"] = exportColumnList[x].bRegex;
			}
			for (var x = 0; x < orderColumnList.length; x++) {
				for (var y = 0; y < exportColumnList.length; y++) {
					if (exportColumnList[y].mData == orderColumnList[x].mData) {
						exportPostingData["order["+x+"][column]"] = y;
						exportPostingData["order["+x+"][dir]"] = orderColumnList[x].order;
						break;
					}
				}
			}
	
			showLoading("<spring:message code='gt.loading.loading' />...");
			
			$.ajax({
	            type: exportPostingType,
	            url: exportPostingURL,
	            data: exportPostingData,
	            success: function(response) {
	                hideLoading();
	            	$("#exportModal").modal('hide');
	            	
	                var blob = blobUtil.base64StringToBlob(response);
	                var downloadUrl = URL.createObjectURL(blob);
	                var a = document.createElement("a");
	                a.href = downloadUrl;
	                var fileName = currentFileName;
	                if (currentExportType == "excel") {
	                	fileName += ".xlsx"
	                } else if (currentExportType == "csv") {
	                	fileName += ".csv"
	                } else if (currentExportType == "pdf") {
	                	fileName += ".pdf"
	                }
	                a.download = fileName;
	                document.body.appendChild(a);
	                a.click();
	            },
	            error: function(XMLHttpRequest, textStatus, errorThrown) {
	            	hideLoading();
	
	           		if (XMLHttpRequest.status == 401) {
	   					showSessionExpired();
	   				} else {
	   					var dialogOption = {};
	   	        		dialogOption.isCloseButton = false;
	   	        		dialogOption.isCloseOnBackground = false;
	   	        		dialogOption.title = "Export Failed!";
	   	        		dialogOption.button1 = {};
	   	        		dialogOption.button1.isShow = true;
	   	        		dialogOption.button1.text = "<spring:message code='gt.button.ok' />";
	   	        		dialogOption.button1.importantFlag = true;
	   	        		dialogOption.button1.fn = function() {
	   	        			hideDialog();
	   	        		};
	   	        		showDialog(dialogOption);
	   				}
	            }       
	        });
		}
	}
	
	function refreshExportDialog() {
		var availableHTML = "";
		for (var x = 0; x < availableColumnList.length; x++) {
			if (!availableColumnList[x].isExportVisible) {
				continue;
			}
			availableHTML += "<option value='"+availableColumnList[x].mData+"'>"+availableColumnList[x].name+"</option>";
		}
		$("#exportModal #export_AvailableSel").html(availableHTML);
	
		var exportHTML = "";
		for (var x = 0; x < exportColumnList.length; x++) {
			if (!exportColumnList[x].isExportVisible) {
				continue;
			}
			exportHTML += "<option value='"+exportColumnList[x].mData+"'>"+exportColumnList[x].name+"</option>";
		}
		$("#exportModal #export_ExportSel").html(exportHTML);
	
		var orderHTML = "";
		for (var x = 0; x < orderColumnList.length; x++) {
			if (!orderColumnList[x].isExportVisible) {
				continue;
			}
			orderHTML += "<option value='"+orderColumnList[x].mData+"'>"+orderColumnList[x].name+" ("+orderColumnList[x].order+")"+"</option>";
		}
		$("#exportModal #export_OrderSel").html(orderHTML);
	}
	
	function moveAvailableToExport(isAll) {
		if (isAll) {
			for (var x = 0; x < availableColumnList.length; x++) {
				exportColumnList.push(availableColumnList[x]);
				availableColumnList.splice(x,1);
				x--;
			}
		} else {
			var selectedData = $("#exportModal #export_AvailableSel").val();
			for (var x = 0; x < availableColumnList.length; x++) {
				if (selectedData.includes(availableColumnList[x].mData)) {
					exportColumnList.push(availableColumnList[x]);
					availableColumnList.splice(x,1);
					x--;
				}
			}
		}
	
		refreshExportDialog();
	}
	
	function moveExportToAvailable(isAll) {
		if (isAll) {
			for (var x = 0; x < exportColumnList.length; x++) {
				availableColumnList.push(exportColumnList[x]);
				exportColumnList.splice(x,1);
				x--;
			}
			orderColumnList = [];
		} else {
			var selectedData = $("#exportModal #export_ExportSel").val();
			for (var x = 0; x < exportColumnList.length; x++) {
				if (selectedData.includes(exportColumnList[x].mData)) {
					for (var y = 0; y < orderColumnList.length; y++) {
						if (orderColumnList[y].mData == exportColumnList[x].mData) {
							orderColumnList.splice(y,1);
							break;
						}
					}
					
					availableColumnList.push(exportColumnList[x]);
					exportColumnList.splice(x,1);
	
					x--;
				}
			}
		}
		
		refreshExportDialog();
	}
	
	function reorderExportUp() {
		var selectedData = $("#exportModal #export_ExportSel").val();
		if (selectedData.length == 0) {
			return;
		} else {
			selectedData = selectedData[0];
		}
		for (var x = 0; x < exportColumnList.length; x++) {
			if (exportColumnList[x].mData == selectedData) {
				if (x-1 >= 0) {
					var moveData = exportColumnList[x];
					exportColumnList.splice(x,1);
					exportColumnList.splice(x-1, 0, moveData);
					
				}
				break;
			}
		}
		
		refreshExportDialog();
		$("#exportModal #export_ExportSel").val(selectedData);
	}
	
	function reorderExportDown() {
		var selectedData = $("#exportModal #export_ExportSel").val();
		if (selectedData.length == 0) {
			return;
		} else {
			selectedData = selectedData[0];
		}
		for (var x = 0; x < exportColumnList.length; x++) {
			if (exportColumnList[x].mData == selectedData) {
				if (x+1 < exportColumnList.length) {
					var moveData = exportColumnList[x];
					exportColumnList.splice(x,1);
					exportColumnList.splice(x+1, 0, moveData);
					
				}
				break;
			}
		}
		
		refreshExportDialog();
		$("#exportModal #export_ExportSel").val(selectedData);
	}
	
	function moveExportToOrder(isAll) {
		if (isAll) {
			for (var x = 0; x < exportColumnList.length; x++) {
				var isExisted = false;
				for (var y = 0; y < orderColumnList.length; y++) {
					if (exportColumnList[x].mData == orderColumnList[y].mData) {
						isExisted = true;
						break;
					}
				}
				if (!isExisted) {
					var orderData = exportColumnList[x];
					orderData.order = "asc";
					orderColumnList.push(orderData);
				}
			}
		} else {
			var selectedData = $("#exportModal #export_ExportSel").val();
			for (var x = 0; x < exportColumnList.length; x++) {
				if (selectedData.includes(exportColumnList[x].mData)) {
					var isExisted = false;
					for (var y = 0; y < orderColumnList.length; y++) {
						if (exportColumnList[x].mData == orderColumnList[y].mData) {
							isExisted = true;
							break;
						}
					}
					if (!isExisted) {
						var orderData = exportColumnList[x];
						orderData.order = "asc";
						orderColumnList.push(orderData);
					}
				}
			}
		}
		
		refreshExportDialog();
	}
	
	function moveOrderToExport(isAll) {
		if (isAll) {
			orderColumnList = [];
		} else {
			var selectedData = $("#exportModal #export_OrderSel").val();
			for (var x = 0; x < orderColumnList.length; x++) {
				if (selectedData.includes(orderColumnList[x].mData)) {
					orderColumnList.splice(x,1);
					x--;
				}
			}
		}
		
		refreshExportDialog();
	}
	
	function changeExportOrder(orderType) {
		var selectedData = $("#exportModal #export_OrderSel").val();
		for (var x = 0; x < orderColumnList.length; x++) {
			if (selectedData.includes(orderColumnList[x].mData)) {
				orderColumnList[x].order = orderType;
			}
		}
	
		refreshExportDialog();
		$("#exportModal #export_OrderSel").val(selectedData);
	}
	
	function reorderOrderUp() {
		var selectedData = $("#exportModal #export_OrderSel").val();
		if (selectedData.length == 0) {
			return;
		} else {
			selectedData = selectedData[0];
		}
		for (var x = 0; x < orderColumnList.length; x++) {
			if (orderColumnList[x].mData == selectedData) {
				if (x-1 >= 0) {
					var moveData = orderColumnList[x];
					orderColumnList.splice(x,1);
					orderColumnList.splice(x-1, 0, moveData);
					
				}
				break;
			}
		}
		
		refreshExportDialog();
		$("#exportModal #export_OrderSel").val(selectedData);
	}
	
	function reorderOrderDown() {
		var selectedData = $("#exportModal #export_OrderSel").val();
		if (selectedData.length == 0) {
			return;
		} else {
			selectedData = selectedData[0];
		}
		for (var x = 0; x < orderColumnList.length; x++) {
			if (orderColumnList[x].mData == selectedData) {
				if (x+1 < orderColumnList.length) {
					var moveData = orderColumnList[x];
					orderColumnList.splice(x,1);
					orderColumnList.splice(x+1, 0, moveData);
					
				}
				break;
			}
		}
		
		refreshExportDialog();
		$("#exportModal #export_OrderSel").val(selectedData);
	}
</script>
<script>
	var currentPage = 'dashboard';
	var app = angular.module("OmniU", [ "ngRoute", "ui.sortable", "xeditable", "angular.filter", "ui.bootstrap", "chart.js", "ui.tinymce", "uiCropper" ]);

	app.filter('custom_trusted_html', ['$sce', function($sce){
        return function(text) {
            return $sce.trustAsHtml(text);
        };
    }]);

	app.directive("customFileRead", [function () {
	    return {
	    	restrict: "A",
	    	require: ['ngModel'],
	        scope: {
	            ngModel: '=',
	            ngFileData: '='
	        },
	        link: function (scope, element, attributes) {
	            element.bind("change", function (changeEvent) {
	            	scope.ngFileData = {};
		            var fileData = {};
		            var fileSize = null;
	                var reader = new FileReader();
	                reader.onload = function (loadEvent) {
		                var tempImageData = new Image();
		                if (loadEvent.target.result.startsWith("data:image")) {
		                	tempImageData.src = loadEvent.target.result;
			                tempImageData.onload = function() {
				                var tempImageWidth = this.width;
				                var tempImageHeight = this.height;
			                	scope.$apply(function () {
			                		scope.ngModel = loadEvent.target.result;

			                		scope.ngFileData.realFile = changeEvent.target.files[0];
				                	scope.ngFileData.imageWidth = tempImageWidth;
				                	scope.ngFileData.imageHeight = tempImageHeight;
				                	scope.ngFileData.fileSize = fileSize;
			                    });
		                    };
			            } else {
			            	scope.$apply(function () {
			            		scope.ngFileData.realFile = changeEvent.target.files[0];
			                	scope.ngFileData.fileSize = fileSize;
		                    	scope.ngModel = loadEvent.target.result;
		                    });
				        }
	                }
	                if (changeEvent.target.files[0]) {
		                reader.readAsDataURL(changeEvent.target.files[0]);
		                fileSize = changeEvent.target.files[0].size;
		            } else {
			            scope.$apply(function () {
			            	scope.ngModel = null;
	                    });
			        }
	            });
	        }
	    }
	}]);

	app.directive('customAngularValidation', function () {
	    return {
	        restrict: 'A',
	        require: 'ngModel',
	        link: function (scope, elem, attrs, ctrl) {
	            scope.$watch(attrs['ngModel'], function () {
	                if (ctrl) {
		                if (Object.keys(ctrl.$error).length > 0) {
		                	elem[0].setCustomValidity(Object.keys(ctrl.$error)[0]);
			            } else {
			            	elem[0].setCustomValidity("");
				        }
	                }
	            });
	        }
	    }
	});

	app.directive('srcImageOnLoad', function() {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
            	attrs.$observe("srcImageOnLoad",function(n,o) {
            		attrs.$set('src', attrs.srcImageOnLoading); 
            		
            		var imageDownload = new Image();
                	imageDownload.onload = function() {
                		attrs.$set('src', imageDownload.src); 
                	};
                	imageDownload.onerror = function() {
                    	if (attrs.srcImageOnLoadFail) {
                    		attrs.$set('src', attrs.srcImageOnLoadFail); 
                        } else {
                        	attrs.$set('src', ''); 
                        }
                	};
                	imageDownload.src = attrs.srcImageOnLoad;
            	});
            }
        };
    });

	app.factory('storageService', ['$rootScope', function($rootScope) {
	    return {
	        get: function(key) {
	            return sessionStorage.getItem(key);
	        },
	        save: function(key, data) {
	            sessionStorage.setItem(key, data);
	        }
	    };
	}]);
	
	app.config(function($routeProvider) {
		$routeProvider
		.when("/",
			{
				redirectTo: '/dashboard'
			})
		.when("/dashboard",
			{
				disableCache: true,
				templateUrl : "${pageContext.request.contextPath}/admin/views/dashboard",
				controller : "dashboard_CTRL"
			})
		.when("/my_profile",
			{
				disableCache: true,
				templateUrl : "${pageContext.request.contextPath}/admin/views/my_profile",
				controller : "my_profile_CTRL"
			})
		.when("/page_management",
			{
				disableCache: true,
				templateUrl : "${pageContext.request.contextPath}/admin/views/page_management",
				controller : "page_management_CTRL"
			})
		.when("/api_management",
			{
				disableCache: true,
				templateUrl : "${pageContext.request.contextPath}/admin/views/api_management",
				controller : "api_management_CTRL"
			})
		.when("/role_management",
			{
				disableCache: true,
				templateUrl : "${pageContext.request.contextPath}/admin/views/role_management",
				controller : "role_management_CTRL"
			})
		.when("/menu_management",
			{
				disableCache: true,
				templateUrl : "${pageContext.request.contextPath}/admin/views/menu_management",
				controller : "menu_management_CTRL"
			})
		.when("/user_management",
			{
				disableCache: true,
				templateUrl : "${pageContext.request.contextPath}/admin/views/user_management",
				controller : "user_management_CTRL"
			})
		.when("/audit_trail",
			{
				disableCache: true,
				templateUrl : "${pageContext.request.contextPath}/admin/views/audit_trail",
				controller : "audit_trail_CTRL"
			})
		.when("/access_denied",
			{
				disableCache: true,
				templateUrl : "${pageContext.request.contextPath}/admin/views/access_denied"
			})
		.when("/under_development",
			{
				disableCache: true,
				templateUrl : "${pageContext.request.contextPath}/admin/views/under_development"
			})
		.when("/under_maintenance",
			{
				disableCache: true,
				templateUrl : "${pageContext.request.contextPath}/admin/views/under_maintenance"
			})
		.when("/404_not_found",
			{
				disableCache: true,
				templateUrl : "${pageContext.request.contextPath}/admin/views/404_not_found"
			})
		.when("/error",
			{
				disableCache: true,
				templateUrl : "${pageContext.request.contextPath}/admin/views/error"
			})
		.when("/session_expired",
			{
				disableCache: true,
				templateUrl : "${pageContext.request.contextPath}/admin/views/session_expired"
			})
		.otherwise({
				disableCache: true,
				templateUrl : "${pageContext.request.contextPath}/admin/views/404_not_found"
			});
	});

	app.run(['editableOptions', function(editableOptions) {
		editableOptions.theme = 'bs4';
	}]);

	app.run(function($rootScope) {
		$rootScope.dataTableLocale = 
			{
			    decimal: "<spring:message code='gt.datatable.decimal' />",
			    emptyTable: "<spring:message code='gt.datatable.empty_table' />",
			    info: "<spring:message code='gt.datatable.info' />",
			    infoEmpty: "<spring:message code='gt.datatable.info_empty' />",
			    infoFiltered: "<spring:message code='gt.datatable.info_filtered' />",
			    infoPostFix: "<spring:message code='gt.datatable.info_post_fix' />",
			    thousands: "<spring:message code='gt.datatable.thousands' />",
			    lengthMenu: "<spring:message code='gt.datatable.length_menu' />",
			    loadingRecords: "<spring:message code='gt.datatable.loading_records' />",
			    processing: "<spring:message code='gt.datatable.processing' />",
			    search: "<spring:message code='gt.datatable.search' />",
			    zeroRecords: "<spring:message code='gt.datatable.zero_records' />",
			    paginate: {
			        first: "<spring:message code='gt.datatable.paginate.first' />",
			        last: "<spring:message code='gt.datatable.paginate.last' />",
			        next: "<spring:message code='gt.datatable.paginate.next' />",
			        previous: "<spring:message code='gt.datatable.paginate.previous' />"
			    },
			    aria: {
			        sortAscending:  "<spring:message code='gt.datatable.aria.sort_ascending' />",
			        sortDescending: "<spring:message code='gt.datatable.aria.sort_descending' />"
			    }
			}
	});

	app.run(function($rootScope, $location, $templateCache, $timeout, storageService) {
		$rootScope.mainMenuData = ${mainMenuData};
		$rootScope.currentActivePage = "";
		
		$rootScope.isDirectedViaMethod = false;

		$rootScope.clearCustomFileReadData = function(inputID) {
			angular.element("#"+inputID)[0].value = null;
		}

		$rootScope.passPageParam = null;

		$rootScope.getPassPageParam = function() {
			return JSON.parse(storageService.get('passPageParam'));
		}
		
		$rootScope.setPassPageParam = function(path, pageParam) {
			var passPageParam = {
				path: path,
				pageParam: pageParam
			}
			storageService.save('passPageParam', JSON.stringify(passPageParam));
		}

		$rootScope.clearPassPageParam = function() {
			storageService.save('passPageParam', null);
		}
		
		$rootScope.goToPath = function (path, paramData) {
			$rootScope.isDirectedViaMethod = true;
			
			if (paramData) {
				$rootScope.setPassPageParam(path, paramData);
			}
			$location.path(path);
		};

		$rootScope.isBackPageBreadCrumbPerformed = false;

		$rootScope.clearPageBreadCrumb = function() {
			storageService.save('pageBreadCrumb', JSON.stringify([]));
		}

		$rootScope.addPageBreadCrumb = function(pageLink) {
			var curPageBreadCrumb = JSON.parse(storageService.get('pageBreadCrumb'));
			if (curPageBreadCrumb == null) {
				curPageBreadCrumb = [];
			}
			curPageBreadCrumb.push({page:pageLink});
			storageService.save('pageBreadCrumb', JSON.stringify(curPageBreadCrumb));
		}

		$rootScope.getPageBreadCrumb = function() {
			return JSON.parse(storageService.get('pageBreadCrumb'));
		}

		$rootScope.getLastPageBreadCrumb = function() {
			var curPageBreadCrumb = JSON.parse(storageService.get('pageBreadCrumb'));
			if (curPageBreadCrumb && curPageBreadCrumb.length > 0) {
				return curPageBreadCrumb[curPageBreadCrumb.length - 1];
			} else {
				return null;
			}
		}

		$rootScope.backToPreviousPageBreadCrumb = function() {
			var curPageBreadCrumb = JSON.parse(storageService.get('pageBreadCrumb'));
			var backPage = curPageBreadCrumb.pop();
			storageService.save('pageBreadCrumb', JSON.stringify(curPageBreadCrumb));

			$rootScope.isBackPageBreadCrumbPerformed = true;
			$rootScope.goToPath(backPage.page);

			$timeout(function() {
				$rootScope.$apply();
			});
		}

		$rootScope.isManualHandleRouteChange = false;
		$rootScope.setManualHandleRouteChange = function(isManualHandle) {
			$rootScope.isManualHandleRouteChange = isManualHandle;
		}

		$rootScope.doRouteHandling = function() {
			hideDialog();
			hideRefreshPage();

			showLoading();
		}
		$rootScope.$on("$routeChangeStart", function(event, next, current) {
			if (!$rootScope.isManualHandleRouteChange) {
				$("body").removeClass("overflow-hidden");
				$rootScope.doRouteHandling();
			}
	    });
		$rootScope.$on("$locationChangeStart", function(event, next, current) {
			if (!$rootScope.isManualHandleRouteChange) {
				$("body").removeClass("overflow-hidden");
				$rootScope.doRouteHandling();
			}
	    });
		$rootScope.$on("$routeChangeSuccess", function(event, next, current) {
			$('html,body').scrollTop(0);
			$rootScope.currentActivePage = $location.path().replace("/","");
			if ($rootScope.getPassPageParam()) {
				if ($rootScope.currentActivePage == $rootScope.getPassPageParam().path) {
					$rootScope.passPageParam = $rootScope.getPassPageParam().pageParam;
				} else {
					$rootScope.clearPassPageParam();
				}
			}
			
			if (next && next.$$route && next.$$route.originalPath.substring(0, next.$$route.originalPath.indexOf("/?")==-1?next.$$route.originalPath.length:next.$$route.originalPath.indexOf("/?")).replace("/","") == "dashboard") {
				//Page Land On Dashboard
				$rootScope.clearPageBreadCrumb();
			} else if (current && current.$$route && next && next.$$route) {
				if (!$rootScope.getLastPageBreadCrumb() || $rootScope.getLastPageBreadCrumb().page != current.$$route.originalPath.substring(0, current.$$route.originalPath.indexOf("/?")==-1?current.$$route.originalPath.length:current.$$route.originalPath.indexOf("/?")).replace("/","")) {
					if (current.$$route.originalPath.substring(0, current.$$route.originalPath.indexOf("/?")==-1?current.$$route.originalPath.length:next.$$current.originalPath.indexOf("/?")).replace("/","") != next.$$route.originalPath.substring(0, next.$$route.originalPath.indexOf("/?")==-1?next.$$route.originalPath.length:next.$$route.originalPath.indexOf("/?")).replace("/","")) {
						if (!$rootScope.isBackPageBreadCrumbPerformed) {
							if (!$rootScope.isDirectedViaMethod) {
								$rootScope.clearPageBreadCrumb();
								if (next.$$route.originalPath.substring(0, next.$$route.originalPath.indexOf("/?")==-1?next.$$route.originalPath.length:next.$$route.originalPath.indexOf("/?")).replace("/","") != "dashboard") {
									$rootScope.addPageBreadCrumb('dashboard');
								}
							} else {
								$rootScope.isDirectedViaMethod = false;
								$rootScope.addPageBreadCrumb(current.$$route.originalPath.substring(0, current.$$route.originalPath.indexOf("/?")==-1?current.$$route.originalPath.length:current.$$route.originalPath.indexOf("/?")).replace("/",""));
							}
						}
					}
				}
			} else if (next && next.$$route && next.$$route.originalPath.substring(0, next.$$route.originalPath.indexOf("/?")==-1?next.$$route.originalPath.length:next.$$route.originalPath.indexOf("/?")).replace("/","") != "dashboard") {
				//Page Refresh On Non-Dashboard
				
			}

			//Check Sub-Menu Header Active
			angular.element(function () {
				angular.element("li.nav-header").each(function() {
				    var isChildActive = false;
					angular.element(this).find("a.collapse-item").each(function() {
						if (angular.element(this).hasClass("active")) {
							isChildActive = true;
						}
					});
					if (isChildActive) {
						angular.element(this).addClass("active");
					} else {
						angular.element(this).removeClass("active");
					}
				});
			});

			$rootScope.isBackPageBreadCrumbPerformed = false;
			hideLoading();
	    });
		$rootScope.errMessage = "";
		$rootScope.$on("$routeChangeError", function (event, current, previous, rejection) {
			hideLoading();
			
			if (current && current.$$route && current.$$route.originalPath.substring(0, current.$$route.originalPath.indexOf("/?")==-1?current.$$route.originalPath.length:current.$$route.originalPath.indexOf("/?")).replace("/","") == "error") {
				showLoadError(rejection);
			} else {
				$rootScope.errMessage = rejection;
				$location.path('error');
			}
		});
		$rootScope.$on('$viewContentLoaded', function() {
			//Remove Cache - Do Not Allow AngularJS To Cache HTML Pages
			// (Using This Will Remove Cache From Template for UI-Bootstrap)
			$templateCache.removeAll();
		});
		$rootScope.isObjOnly = function(val) {
			return typeof val === 'object' && !Array.isArray(val);
		}
		$rootScope.copyToClipboard = function(text) {
			var $temp = document.createElement('textarea');
		    $("body").append($temp);
		    $temp.innerHTML = text;
		    $temp.select();
		    document.execCommand("copy");
		    $temp.remove();
		};
	});
</script>
<jsp:include page="/WEB-INF/admin/controller/dashboard_CTRL.jsp" />
<jsp:include page="/WEB-INF/admin/controller/my_profile_CTRL.jsp" />
<jsp:include page="/WEB-INF/admin/controller/page_management_CTRL.jsp" />
<jsp:include page="/WEB-INF/admin/controller/api_management_CTRL.jsp" />
<jsp:include page="/WEB-INF/admin/controller/role_management_CTRL.jsp" />
<jsp:include page="/WEB-INF/admin/controller/menu_management_CTRL.jsp" />
<jsp:include page="/WEB-INF/admin/controller/user_management_CTRL.jsp" />
<jsp:include page="/WEB-INF/admin/controller/audit_trail_CTRL.jsp" />
<script>
$.ajax({
	method: "POST",
	url: "${pageContext.request.contextPath}/admin_api/get_pwa_status",
	data: { }
}).done(function( data ) {
	if (!data.isPWA) {
		if ((typeof(caches) !== "undefined")) {
			caches.keys().then(function(keys) {
			    for (let key of keys)
			        caches.delete(key);
			});
			navigator.serviceWorker.getRegistrations().then(function(registrations) {
				for(let registration of registrations)
					registration.unregister();
			});
	    } else {
			console.log('[SW] Not Available in HTTP');
		}
	}
});

if ('serviceWorker' in navigator) {
	if (<%=isAllowPWA%>) {
		if ((typeof(caches) !== "undefined")) {
			navigator.serviceWorker.register('sw.js')
			.then(reg => console.log('[SW] Registered'))
			.catch(err => console.log('[SW] Registration Error: ', err));
		} else {
			console.log('[SW] Not Available in HTTP');
		}
	} else {
		if ((typeof(caches) !== "undefined")) {
			console.log('[SW] Disabled');
			
			caches.keys().then(function(keys) {
			    for (let key of keys)
			        caches.delete(key);
			});
			navigator.serviceWorker.getRegistrations().then(function(registrations) {
				for(let registration of registrations)
					registration.unregister();
			});
		} else {
			console.log('[SW] Not Available in HTTP');
		}
	}
}
</script>
</html>