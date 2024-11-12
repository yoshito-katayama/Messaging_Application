/** MQのアドレスを取得しボタンに設定する関数 **/
document.addEventListener('DOMContentLoaded', async function() {            
    let res = await performGetQMAddress();
    let address = res.data;
    if(res.status >=200 && res.status<300) {

    }          
    var addressElement = document.getElementById('mqConsoleBtn').getAttribute('connName');            
    mqConsoleBtn.setAttribute('connName', address);       
});

/** MQのコンソールを開く関数 **/
const openMQConsole = () => {                  
  var address = document.getElementById('mqConsoleBtn').getAttribute('connName');                      
  let URL = `https://${address}:9443/ibmmq/console/login.html`
  window.open(URL, '_blank').focus();
}

/** MQのアドレスを取得する関数 **/
const performGetQMAddress = async () => {
  try {
    /** `http://localhost:8080/address` DemoController.javaへ **/
    const response = await fetch(`http://localhost/address`);
    /** エラーチェック **/
    if (!response.ok) {
      return { status: false, data: "Some errors occured" };
    }
    /** 実行結果を返す **/
    const data = await response.json(); 
    return { status: true, data: data.message };
  } catch (err) {
    return { status: false, data: err };
  }
};

/** メッセージをMQサーバに送信する関数 **/
const performSend = async (msg) => {
  try {
    /** `http://localhost:8080/send` DemoController.javaへ **/
    const response = await fetch(
      `http://localhost:8080/send?msg=${encodeURIComponent(msg)}`
    );
    /** エラーチェック **/
    if (!response.ok) {
      return { status: false, data: "Some errors occured" };
    }
    /** 実行結果を返す **/
    const data = await response.json();
    return { status: true, data: data.message };
  } catch (err) {
    return { status: false, data: err.message };
  }
};

/** メッセージをMQサーバから受信する関数 **/
const performGet = async () => {
  try {
    /** `http://localhost:8080/recv` DemoController.javaへ **/
    const response = await fetch(`http://localhost:8080/recv`);
    /** エラーチェック **/
    if (!response.ok) {
      return { status: false, data: "Some errors occured" };
    }
    /** 実行結果を返す **/
    const data = await response.json();
    return { status: true, data: data.message };
  } catch (err) {
    return { status: false, data: err };
  }
};

/** メッセージ送信時のUIとエラーチェック関数 **/
async function sendMessage() {
  /** メッセージの受け取り **/
  const messageInput = document.getElementById("message-input");
  const messageText = messageInput.value.trim();
  /** メッセージが空の場合のポップアップ **/
  if (messageText === "") {
    alert("Please insert a valid message.");
    return;
  }
  /** スピナーの表示 **/
  const spinner = document.querySelector(".spinner");
  spinner.style.display = "block";
  /** メッセージをMQサーバに送信 **/
  let res = await performSend(messageText);
  /** メッセージを画面に表示 **/
  if (res.status === true) {
    addMessage(res.data);
  }
  /** スピナーの非表示 **/
  spinner.style.display = "none";
}

/** MQサーバーからメッセージを受信する関数 **/
async function getMessage() {
  /** スピナーの表示 **/
  const spinner = document.querySelector(".spinner");
  spinner.style.display = "block";
  /** メッセージをMQサーバから受信 **/
  let res = await performGet();
  /** メッセージを画面に表示 **/
  if (res.status == true) {
    addMessage(res.data);
  }
  /** スピナーの非表示 **/
  spinner.style.display = "none";
}

/** メッセージを画面に表示する関数 **/
const addMessage = (msg) => {
  const chatContainer = document.querySelector(".chat-container");
  const message = document.createElement("div");
  message.className = "message";
  message.textContent = msg;
  chatContainer.appendChild(message);
  chatContainer.scrollTop = chatContainer.scrollHeight;
};

/** Db2にメッセージをインサートする関数 **/
async function insertMessage() {
  /** メッセージの受け取り **/
  const messageInput = document.getElementById("message-insert");
  const messageText = messageInput.value.trim();
  /** メッセージが空の場合のポップアップ **/
  if (messageText === "") {
    alert("Please insert a valid message.");
    return;
  }
  /** スピナーの表示 **/
  const spinner = document.querySelector(".spinner");
  spinner.style.display = "block";
  /** メッセージをMQサーバに送信 **/
  let res = await performInsert(messageText);
  /** メッセージを画面に表示 **/
  if (res.status === true) {
    addMessage(res.data);
  }
  /** スピナーの非表示 **/
  spinner.style.display = "none";
}

/** Db2にメッセージをインサートする関数 **/
const performInsert = async (msg) => {
  try {
    /** `http://localhost:8080/insert` DemoController.javaへ **/
    const response = await fetch("http://localhost:8080/insert", {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: new URLSearchParams({ msg: msg }),
    });

    /** エラーチェック **/
    if (!response.ok) {
      return { status: false, data: "Some errors occured" };
    }
    /** 実行結果を返す **/
    const data = await response.json();
    return { status: true, data: data.message };
  } catch (err) {
    return { status: false, data: err.message };
  }
};

/** Db2のコンテンツを確認する関数 **/
async function checkDb2Contents() {
  /** スピナーの表示 **/
  const spinner = document.querySelector(".spinner");
  spinner.style.display = "block";
  /** Db2サーバから受信 **/
  let res = await performSelect();
  /** メッセージを画面に表示 **/
  if (res.status == true) {
    addMessage(res.data);
  }
  /** スピナーの非表示 **/
  spinner.style.display = "none";
}

/** Db2のコンテンツを確認する関数 **/
const performSelect = async () => {
  try {
    /** `http://localhost:8080/check` DemoController.javaへ **/
    const response = await fetch(`http://localhost:8080/check`);
    /** エラーチェック **/
    if (!response.ok) {
      return { status: false, data: "Some errors occured" };
    }
    /** 実行結果を返す **/
    const data = await response.json();
    return { status: true, data: data.message };
  } catch (err) {
    return { status: false, data: err };
  }
};

/** メッセージ送信時のUIとエラーチェック関数 **/
async function registerMessage() {
  /** メッセージの受け取り **/
  const messageInput = document.getElementById("message-register");
  const messageText = messageInput.value.trim();
  /** メッセージが空の場合のポップアップ **/
  if (messageText === "") {
    alert("Please insert a valid message.");
    return;
  }
  /** スピナーの表示 **/
  const spinner = document.querySelector(".spinner");
  spinner.style.display = "block";
  /** メッセージをMQサーバに送信 **/
  let res = await performSend(messageText);
  /** メッセージを画面に表示 **/
  if (res.status === true) {
    addMessage(res.data);
  }
  /** メッセージをMQサーバから受信 **/
  let res2 = await performGet();
  /** メッセージを画面に表示 **/
  if (res2.status == true) {
    addMessage(res2.data);
  }
  /** メッセージをMQサーバに送信 **/
  let res3 = await performInsert(messageText);
  /** メッセージを画面に表示 **/
  if (res3.status === true) {
    addMessage(res3.data);
  }
  /** スピナーの非表示 **/
  spinner.style.display = "none";
}