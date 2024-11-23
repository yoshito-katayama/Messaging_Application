/** MQのアドレスを取得しボタンに設定する関数 **/
document.addEventListener('DOMContentLoaded', async function() {            
    let res = await performGetQMAddress();
    let address = res.data;
    if(res.status >=200 && res.status<300) {

    }          
    var addressElement = document.getElementById('mqConsoleBtn').getAttribute('connName');            
    mqConsoleBtn.setAttribute('connName', address);       
});

/** MQのアドレスを取得する関数 **/
const performGetQMAddress = async () => {
  try {
    const response = await fetch(`https://app-hands-on-test.cluster-mdpd-container-e0f3feaa6b4ef88a2938a1815281ffc2-0000.jp-tok.containers.appdomain.cloud/address`);
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
    const response = await fetch(`https://app-hands-on-test.cluster-mdpd-container-e0f3feaa6b4ef88a2938a1815281ffc2-0000.jp-tok.containers.appdomain.cloud/send?msg=${encodeURIComponent(msg)}`
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

/** メッセージ送信時のUIとエラーチェック関数 **/
async function sendMessage() {
  /** メッセージの受け取り **/
  const nameInput = document.getElementById("name-input");
  const nameText = nameInput.value.trim();
  const messageInput = document.getElementById("message-input");
  const messageText = messageInput.value.trim();
  /** メッセージが空の場合のポップアップ **/
  if (nameText === "") {
    alert("Please insert a valid name.");
    return;
  }
  if (messageText === "") {
    alert("Please insert a valid message.");
    return;
  }
  const Text = nameText + " ： " + messageText;
  /** スピナーの表示 **/
  const spinner = document.querySelector(".spinner");
  spinner.style.display = "block";
  /** メッセージをMQサーバに送信 **/
  let res = await performSend(Text);
  /** スピナーの非表示 **/
  spinner.style.display = "none";
  /** メッセージ入力フィールドをリセット **/
  messageInput.value = "";
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

document.addEventListener('DOMContentLoaded', async function() {
  await checkDb2Contents();
})

function reload(){
  window.location.reload();
}

/** Db2のコンテンツを確認する関数 **/
async function checkDb2Contents() {
  /** スピナーの表示 **/
  const spinner = document.querySelector(".spinner");
  spinner.style.display = "block";
  /** Db2サーバから受信 **/
  let res = await performSelect();
  /** JSONに変換 **/
  const cleanString = res.data.replace("Message Contents: ", "");
  const cleanedStringWithoutBrackets = cleanString.replace(/^\[|\]$/g, '').trim();
  const messageArray = cleanedStringWithoutBrackets.split(",").map(item => item.trim());
  const filteredArray = messageArray.filter(item => item !== "null" && item !== "");
  const jsonObject = { message: filteredArray };
  console.log(jsonObject);
  /** メッセージを画面に表示 **/
  for (let i = 0; i < jsonObject.message.length; i++) {
    addMessage(jsonObject.message[i]);
  }
  /** スピナーの非表示 **/
  spinner.style.display = "none";
}

/** Db2のコンテンツを確認する関数 **/
const performSelect = async () => {
  try {
    const response = await fetch(`https://app-hands-on-test.cluster-mdpd-container-e0f3feaa6b4ef88a2938a1815281ffc2-0000.jp-tok.containers.appdomain.cloud/check`);
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