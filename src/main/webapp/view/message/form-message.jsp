<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 19.04.2017
  Time: 2:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html>
<head>
    <c:choose>
        <c:when test="${list.size() > 0}">
            <c:set value="${list.get(0).getDialog().getSecond()}" var="im"/>
            <c:set value="${list.get(0).getDialog().getId()}" var="dialogId"/>
        </c:when>
        <c:otherwise>
            <c:set value="${im}" var="im"/>
            <c:set value="${dialogId}" var="dialogId"/>
        </c:otherwise>
    </c:choose>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"\>
    <title>${im.getFirstName()} ${im.getLastName()}</title>
    <style>
        .block-sms {
            height: 500px;
            width: 600px;
            overflow: scroll;
            word-break:break-all;
            overflow-x:hidden;
        }
    </style>
</head>
<body>
<%@include file="../index/header.jsp" %>
<div class="col-lg-6 col-lg-offset-2" >
    <a href="/user/id/${im.getId()}" >
        <h1 class="text-center text-primary">${im.getFirstName()} ${im.getLastName()}</h1>
    </a>
    <hr/>
    <div id="scroll" class="form-group block-sms scroll-down">

        <table class="table table-hover">
            <thead>
            <tr>
                <td></td>
                <td></td>
            </tr>
            </thead>
            <tbody id="tbodyMessage">
            <c:set var="size" value="${list.size()}"/>
            <c:forEach var = "i" begin = "1" end = "${size}">
                <c:set var="a" value="${list.get(list.size() - i)}"/>
                <tr>
                    <td>
                        <p>
                            <a href="/user/id/${a.getSender().getId()}">${a.getSender().getFirstName()} ${a.getSender().getLastName()} </a>
                        </p>
                        <p>${a.getMessage()}</p>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>

    <form id="formMessage">
        <div class="form-group">
            <textarea id="inputMessage" placeholder="write message............" name="message" class="form-control" style="height: 100px " rows="5" ></textarea>
        </div>

        <div class="col-lg-12 " >
            <button form="formMessage" style="width: 100%" class="btn btn-info">
                Send <span class="glyphicon glyphicon-ok"/>
            </button>
        </div>
    </form>
</div>



<script>


    function message(sel) {
        $.ajax({
            url: "/im/getMessage?sel="+sel,
            type:"GET",
            contentType:"application/json; charset=UTF-8",
            dataType: "json",
            success:function (data) {
                    createTableMessage(data, sel),
                    scrollDown();
                    }
        });
    }

    function createTableMessage(data) {
        if (data) {
            var jsonData = data;
            var result;
            for (var i = jsonData.length - 1; i >= 0; i--) {
                var element = jsonData[i];
                var user = element.sender;
                var table = ' <tr><td><p><a href="/user/id/' + user.userId + '">'
                    + user.firstName +
                    ' ' + user.lastName +
                    '</a>' +
                    '</p> ' +
                    '<p>' + element.message + '</p> ' +
                    '</td> </tr>';
                result = result + table;
            }
            $("#scroll #tbodyMessage")
                .html(result);
        }
    }

    $(document).ready(function () {
        $("#formMessage").submit(function () {
            var message;
            $.ajax({
                url: "/im/save",
                type: "POST",
                data: $("#formMessage").serialize(),
                beforeSend: function () {
                    message = $("#formMessage #inputMessage").val();
                    $("#inputMessage").val("");
                },
                success: function () {
                    writeMessage(
                        ${authUser.getId()},
                        '${authUser.getFirstName()}',
                        '${authUser.getLastName()}',
                        message
                    );
                }
            });
            return false;
        });
    });

    function writeMessage(sel, first, last, message) {
        var table = ' <tr><td><p><a href="/user/id/' + sel + '">'
            + first +
            ' ' + last +
            '</a>' +
            '</p> ' +
            '<p>' + message + '</p> ' +
            '</td> </tr>';
        $("#scroll #tbodyMessage").append(table);
        scrollDown();
    }

    function scrollDown() {
        var div = $(".scroll-down");
        div.scrollTop(div.prop('scrollHeight'));
    }

    $(document).ready(function () {
            scrollDown();})

    <c:if test="${dialogId != -1}">
        window.setInterval(
            function () {
                message(${im.getId()});
                scrollDown();
            }, 6000
        )
    </c:if>
</script>




</body>
</html>