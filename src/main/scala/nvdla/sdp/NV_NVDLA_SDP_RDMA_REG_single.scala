// package nvdla

// import chisel3._
// import chisel3.experimental._
// import chisel3.util._

// class NV_NVDLA_SDP_RDMA_REG_single extends Module {
//     val io = IO(new Bundle {
//         // clk
//         val nvdla_core_clk = Input(Clock())

//         // Register control interface
//         val reg = new reg_control_if

//         // producer, consumer, status
//         val producer = Output(Bool())
        
//         val consumer = Input(Bool())
//         val status_0 = Input(UInt(2.W))
//         val status_1 = Input(UInt(2.W))    
//     })
// //     
// //          ┌─┐       ┌─┐
// //       ┌──┘ ┴───────┘ ┴──┐
// //       │                 │
// //       │       ───       │          
// //       │  ─┬┘       └┬─  │
// //       │                 │
// //       │       ─┴─       │
// //       │                 │
// //       └───┐         ┌───┘
// //           │         │
// //           │         │
// //           │         │
// //           │         └──────────────┐
// //           │                        │
// //           │                        ├─┐
// //           │                        ┌─┘    
// //           │                        │
// //           └─┐  ┐  ┌───────┬──┐  ┌──┘         
// //             │ ─┤ ─┤       │ ─┤ ─┤         
// //             └──┴──┘       └──┴──┘ 
//     withClock(io.nvdla_core_clk){
//     // Address decode

//     val nvdla_sdp_rdma_s_pointer_0_wren = (io.reg.offset === "h4".asUInt(32.W))&io.reg.wr_en
//     val nvdla_sdp_rdma_s_status_0_wren = (io.reg.offset === "h0".asUInt(32.W))&io.reg.wr_en

//     // Output mux
   
//     io.reg.rd_data := MuxLookup(io.reg.offset, "b0".asUInt(32.W), 
//     Seq(  
//     //nvdla_sdp_rdma_s_pointer_0_out    
//     "h4".asUInt(32.W)  -> Cat("b0".asUInt(15.W), io.consumer, "b0".asUInt(15.W), io.producer),
//     //nvdla_sdp_rdma_s_status_0_out
//     "h0".asUInt(32.W)  -> Cat("b0".asUInt(14.W), io.status_1, "b0".asUInt(14.W), io.status_0)
//     ))

//     io.producer := RegEnable(io.reg.wr_data(0), false.B, nvdla_sdp_rdma_s_pointer_0_wren)

// }}

// object NV_NVDLA_SDP_RDMA_REG_singleDriver extends App {
//   chisel3.Driver.execute(args, () => new NV_NVDLA_SDP_RDMA_REG_single())
// }

