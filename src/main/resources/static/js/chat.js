let contacts = [];
let userThread = [];

$(function(){
    $(".heading-compose").click(function() {
        $(".side-two").css({
            "left": "0"
        });
    });

    $(".newMessage-back").click(function() {
        $(".side-two").css({
            "left": "-100%"
        });
    });

    let contactSearchBox = $("#composeText");
    let contactSearchAction = $("#search");
    contactSearchBox.keydown(function (e) {
        let key = e.which;
        if (key === 13) {
            contactSearchAction.click();
            return false;
        }
    });

    const getNewContactHtml = function (userId, firstname, lastname, isNewContact) {
        return `            <div id="new-contact" data-id=${userId} data-new=${isNewContact} data-firstname=${firstname} data-lastname=${lastname} class="row sideBar-body">
                                <div class="col-sm-3 col-xs-3 sideBar-avatar">
                                    <div class="avatar-icon">
                                        <img src="https://bootdey.com/img/Content/avatar/avatar1.png">
                                    </div>
                                </div>
                                <div class="col-sm-9 col-xs-9 sideBar-main">
                                    <div class="row">
                                        <div class="col-sm-8 col-xs-8 sideBar-name">
                                            <span class="name-meta">${firstname} ${lastname}</span>
                                        </div>
                                        <div class="col-sm-4 col-xs-4 pull-right sideBar-time">
                                            <span class="text-primary pull-right">${isNewContact ? "New!" : "Start Chat"}</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
        `;
    }

    const getContactNotFoundHtml = function (message) {
        return `            <div class="row sideBar-body">
                            <div class="col-sm-9 col-xs-9 sideBar-main">
                                <div class="row">
                                    <div class="col-sm-8 col-xs-8 sideBar-name">
                                        <span class="name-meta">${message}</span>
                                    </div>
                                </div>
                            </div>
                     </div>
        `;
    }

    contactSearchAction.click(function() {
        let term = contactSearchBox.val();
        console.log(term + " in click and clear all search result");

        $("#searchResult").empty();

        $.ajax({
            url: "/chat/search-friend?username="+term,
            type: 'get',
            success: function (data) {
                console.log(data);
                if (data.success) {
                    let userId = data.user.id;
                    let firstname = data.user.firstName;
                    let lastname = data.user.lastName;
                    let isNewContact = data.new;
                    $("#searchResult").prepend(getNewContactHtml(userId, firstname, lastname, isNewContact));
                } else {
                    $("#searchResult").prepend(getContactNotFoundHtml(data.message));
                }
            },
            error: function (error) {
                console.error(error)
                window.location.href = "/";
            }
        });
    });

    $('#show-contacts').click(function () {
        if (contacts.length === 0) {
            $.ajax({
                url: "/chat/list-contacts",
                type: 'get',
                success: function (data) {
                    console.log(data);
                    if (data.success) {
                        let users = data.contacts;
                        for (let user of users) {
                            contacts.push(user);
                            $('#contact-list').append(getContactHtml(user.id, user.firstName, user.lastName));
                        }
                    }
                },
                error: function (error) {
                    console.error(error);
                }
            });
        } else {
            console.log(contacts);
        }
    })
});

const getContactHtml = function (userId, firstname, lastname) {
    return `            <div data-id=${userId} class="row sideBar-body contact">
                                <div class="col-sm-3 col-xs-3 sideBar-avatar">
                                    <div class="avatar-icon">
                                        <img src="https://bootdey.com/img/Content/avatar/avatar1.png">
                                    </div>
                                </div>
                                <div class="col-sm-9 col-xs-9 sideBar-main">
                                    <div class="row">
                                        <div class="col-sm-8 col-xs-8 sideBar-name">
                                            <span class="name-meta">${firstname} ${lastname}</span>
                                        </div>
                                        <div class="col-sm-4 col-xs-4 pull-right sideBar-time">
                                            <span class="text-primary pull-right">Chat!</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
        `;
}

const getThreadHtml = function (threadId, threadName, updatedAt) {
    return `            <div data-thread-id=${threadId} class="row sideBar-body thread">
                                <div class="col-sm-3 col-xs-3 sideBar-avatar">
                                    <div class="avatar-icon">
                                        <img src="https://bootdey.com/img/Content/avatar/avatar1.png">
                                    </div>
                                </div>
                                <div class="col-sm-9 col-xs-9 sideBar-main">
                                    <div class="row">
                                        <div class="col-sm-8 col-xs-8 sideBar-name">
                                            <span class="name-meta">${threadName}</span>
                                        </div>
                                        <div class="col-sm-4 col-xs-4 pull-right sideBar-time">
                                            <span class="text-primary pull-right">${updatedAt}</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
        `;
}

const buildUser = function (userId, firstname, lastname) {
    return {
        id: userId,
        firstName: firstname,
        lastName: lastname
    };
}

const buildThread = function (threadId, threadName, updatedAt, updatedDate) {
    return {
        threadId: threadId,
        threadName: threadName,
        updatedAt: updatedAt,
        updatedDate: updatedDate,
        unreadCount: 0,
        lastMessage: ""
    };
}

function startChatWithUserIds(userId) {
    $.ajax({
        url: "/chat/load-thread",
        type: 'post',
        data: JSON.stringify({userIds: [userId]}),
        dataType: 'json',
        contentType: "application/json; charset=utf-8",
        success: function (data) {
            console.log(data);
            if (data.success) {
                let threadId = data.threadId;
                let thread = userThread.find(t => t.threadId === threadId);
                console.log(thread);

                $(`div[data-thread-id='${threadId}']`).remove();
                $('#thread-list').prepend(getThreadHtml(thread.threadId, thread.threadName, thread.updatedAt));

                $(".side-two").css({
                    "left": "-100%"
                });
            }
        },
        error: function (error) {
            console.error(error);
        }
    });
}

$('body').on('click', 'div#new-contact', function () {
    let userId = $(this).data("id");
    let isNewContact = $(this).data("new");
    let firstname = $(this).data("firstname");
    let lastname = $(this).data("lastname");
    console.log(userId + " " + isNewContact + " " + firstname + " " + lastname);
    console.log(typeof userId);
    console.log(typeof isNewContact);

    if (isNewContact) {
        $.ajax({
            url: "/chat/add-friend?userId="+userId,
            type: 'get',
            success: function (data) {
                console.log(data);
                if (data.success) {
                    $(".side-two").css({
                        "left": "-100%"
                    });

                    $("#searchResult").empty();
                    contacts.push(buildUser(userId, firstname, lastname));
                    $('#contact-list').append(getContactHtml(userId, firstname, lastname));
                    let newThread = buildThread(data.threadId, data.threadName, data.updatedAt, data.updatedDate);
                    userThread.push(newThread);
                    $('#thread-list').prepend(getThreadHtml(newThread.threadId, newThread.threadName, newThread.updatedAt));
                }
            },
            error: function (error) {
                console.error(error);
            }
        })
    } else {
        startChatWithUserIds(userId);
    }
})

$(document).ready(function () {
    userThread = [];
    $.ajax({
        url: "/chat/load-user-threads",
        type: 'get',
        async: false,
        cache: false,
        success: function (data) {
            console.log(data);
            if (data.success) {
                let threads = data.threads;
                for (let thread of threads) {
                    userThread.push(thread);
                    $('#thread-list').append(getThreadHtml(thread.threadId, thread.threadName, thread.updatedAt));
                }
            }
        },
        error: function (e) {
            console.error(e);
        }
    });
})

$('body').on('click', 'div.contact', function () {
    let userId = $(this).data("id");
    startChatWithUserIds(userId);
})