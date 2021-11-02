package com.example.hellogrpc

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.examples.helloworld.GreeterGrpc
import io.grpc.examples.helloworld.HelloRequest
import org.springframework.web.client.RestTemplate
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.send_grpc_button).setOnClickListener {
            Thread {
                val channel: ManagedChannel = ManagedChannelBuilder.forAddress("192.168.1.90", 50051).usePlaintext().build()
                val greeterStub = GreeterGrpc.newBlockingStub(channel)
                val request = HelloRequest.newBuilder().setName("Akram").build();
                val result = try {
                    val reply = greeterStub.sayHello(request)
                    reply.message
                } catch (e: Exception) {
                    String.format("Failed... : %n%s", e.stackTraceToString())
                }
                println(result)
                try {
                    channel.shutdown().awaitTermination(1, TimeUnit.SECONDS)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }.start()
        }
        findViewById<Button>(R.id.send_http_button).setOnClickListener {
            Thread {
                val restTemplate = RestTemplate()
                val result = try {
                    val s = restTemplate.postForEntity("http://192.168.1.90:8080", "Akram", String::class.java)
                    s.body
                } catch (e: java.lang.Exception) {
                    String.format("Failed... : %n%s", e.stackTraceToString())
                }
                println(result)
            }.start()
        }
    }
}