import socket
import threading
from kivy.app import App
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.textinput import TextInput
from kivy.uix.button import Button
from kivy.core.clipboard import Clipboard
from kivy.storage.jsonstore import JsonStore

store = JsonStore("config.json")

class Root(BoxLayout):
    def __init__(self, **kwargs):
        super().__init__(orientation="vertical", **kwargs)

        self.ip = TextInput(
            hint_text="Enter server IP",
            multiline=False
        )
        if store.exists("server"):
            self.ip.text = store.get("server")["ip"]

        self.add_widget(self.ip)

        self.btn = Button(text="Send Clipboard")
        self.btn.bind(on_press=self.send_clipboard)
        self.add_widget(self.btn)

    def send_clipboard(self, _):
        ip = self.ip.text.strip()
        store.put("server", ip=ip)

        data = Clipboard.paste()

        try:
            s = socket.socket()
            s.connect((ip, 3423))
            s.send(data.encode())
            s.close()
        except Exception as e:
            print("Error:", e)

class ClipSyncApp(App):
    def build(self):
        return Root()

ClipSyncApp().run()
